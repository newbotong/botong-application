package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.config.RedisApproval;
import com.yunjing.approval.dao.cache.ApprovalRedisService;
import com.yunjing.approval.dao.mapper.ApprovalAttrMapper;
import com.yunjing.approval.dao.mapper.ApprovalMapper;
import com.yunjing.approval.dao.mapper.ModelItemMapper;
import com.yunjing.approval.excel.*;
import com.yunjing.approval.model.dto.CompanyDTO;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.param.DataParam;
import com.yunjing.approval.processor.task.async.ApprovalPushTask;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.approval.util.EmojiFilterUtils;
import com.yunjing.mommon.Enum.DateStyle;
import com.yunjing.mommon.global.exception.DeleteMessageFailureException;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.global.exception.MessageNotExitException;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.PageWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * @author roc
 * @date 2018/1/15
 */
@Service
public class ApprovalServiceImpl extends BaseServiceImpl<ApprovalMapper, Approval> implements IApprovalService {

    private final Log logger = LogFactory.getLog(ApprovalServiceImpl.class);
    @Autowired
    private IApprovalProcessService approvalProcessService;
    @Autowired
    private IApprovalAttrService approvalAttrService;
    @Autowired
    private ICopysService copySService;
    @Autowired
    private IApprovalService approvalService;
    @Autowired
    private IExportLogService exportLogService;

    @Autowired
    private ModelItemMapper modelItemMapper;
    @Autowired
    private IModelService modelService;
    @Autowired
    private ApprovalAttrMapper approvalAttrMapper;
    @Autowired
    private ApprovalRedisService approvalRedisService;
    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private ApprovalPushTask approvalPushTask;
    @Autowired
    private RedisApproval redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submit(String companyId, String memberId, String modelId, JSONArray jsonData, String sendUserIds, String sendCopyIds) throws Exception {
        logger.info("companyId: " + companyId + " memberId: " + memberId + " modelId: " + modelId + " jsonData： " + jsonData.toJSONString() + " sendUserIds： " + sendUserIds + " sendCopyIds: " + sendCopyIds);
        ModelL modelL = modelService.selectById(modelId);
        Approval approval = new Approval();
        approval.setId(IDUtils.uuid());
        approval.setModelId(modelId);
        approval.setOrgId(companyId);
        approval.setUserId(memberId);
        approval.setCreateTime(System.currentTimeMillis());
        approval.setState(0);
        approval.setModelVersion(modelL.getModelVersion());
        // 处理审批标题
        String title = "不存在的";
        if (memberId == null) {
            title += "用户";
        } else {
            ApprovalUser user = approvalUserService.selectOne(Condition.create().where("id={0}", memberId));
            String nick = "";
            if (user != null) {
                nick = user.getName();
            }
            String name = modelL.getModelName();

            if (StringUtils.isBlank(nick)) {
                title += "用户昵称";
            } else if (StringUtils.isBlank(name)) {
                title += "模型名称";
            } else {
                title = nick + "的" + modelL.getModelName();
            }
        }
        approval.setTitle(title);

        // 保存审批
        boolean insert = this.insert(approval);
        if (!insert) {
            throw new InsertMessageFailureException("保存审批信息失败");
        }
        Set<ApprovalAttr> attrSet = new HashSet<>();
        Set<ApprovalAttr> contentSet = new HashSet<>();
        // 解析并保存审批信息
        Iterator<Object> it = jsonData.iterator();
        while (it.hasNext()) {
            JSONObject obj = (JSONObject) it.next();
            int type = obj.getIntValue("type");
            String name = obj.getString("field");
            String value = obj.getString("value");
            String val;
            // 类型是10-图片情况
            if (type == ApproConstants.PICTURE_TYPE_10) {
                JSONArray array = obj.getJSONArray("value");
                if (array != null && !array.isEmpty()) {
                    ApprovalAttr attr = new ApprovalAttr();
                    attr.setId(IDUtils.uuid());
                    attr.setApprovalId(String.valueOf(approval.getId()));
                    attr.setAttrName(name);
                    attr.setAttrType(type);
                    val = array.toJSONString();
                    attr.setAttrValue(EmojiFilterUtils.filterEmoji(val));
                    attrSet.add(attr);
                }
            } else if (type == ApproConstants.DETAILED_TYPE_7) {
                // 明细类型
                JSONArray array = obj.getJSONArray("content");
                Iterator<Object> content = array.iterator();
                while (content.hasNext()) {
                    ApprovalAttr attr1 = new ApprovalAttr();
                    attr1.setId(IDUtils.uuid());
                    attr1.setApprovalId(approval.getId());
                    JSONObject contents = (JSONObject) content.next();
                    JSONArray array1 = contents.getJSONArray("items");
                    String field = contents.getString("field");
                    Integer num = contents.getInteger("num");
                    attr1.setAttrType(type);
                    if (StringUtils.isNotBlank(field)){
                        attr1.setAttrName(field);
                    }else {
                        attr1.setAttrName("mingxi");
                    }
                    contentSet.add(attr1);
                    Iterator<Object> modelItems = array1.iterator();
                    Set<ApprovalAttr> attrs = new HashSet<>();
                    while (modelItems.hasNext()) {
                        JSONObject detail = (JSONObject) modelItems.next();
                        int detailType = detail.getIntValue("dataType");
                        String detailName = detail.getString("field");
                        String detailValue = detail.getString("value");
                        if (StringUtils.isNotBlank(detailValue)) {
                            ApprovalAttr entity = new ApprovalAttr();
                            entity.setId(IDUtils.uuid());
                            entity.setApprovalId(String.valueOf(approval.getId()));
                            entity.setAttrParent(String.valueOf(attr1.getId()));
                            entity.setAttrName(detailName);
                            entity.setAttrType(detailType);
                            String detailValues = "";
                            // 明细中类型是10-图片的情况
                            if (detailType == ApproConstants.PICTURE_TYPE_10) {
                                JSONArray detailArray = detail.getJSONArray("value");
                                if (detailArray != null && !detailArray.isEmpty()) {
                                    detailValue = detailArray.toJSONString();
                                }
                                entity.setAttrValue(EmojiFilterUtils.filterEmoji(detailValue));
                            } else if (detailType == ApproConstants.TIME_INTERVAL_TYPE_5) {
                                detailValues = detail.getString("values");
                                entity.setAttrValue(EmojiFilterUtils.filterEmoji(detailValue) + "," + detailValues);
                            } else {
                                entity.setAttrValue(EmojiFilterUtils.filterEmoji(detailValue));
                            }
                            entity.setAttrNum(num);
                            attrs.add(entity);
                        }
                    }
                    List<ApprovalAttr> attrList = new ArrayList<>(attrs);
                    if (!attrList.isEmpty()) {
                        boolean b = approvalAttrService.insertBatch(attrList);
                        if (!b) {
                            throw new InsertMessageFailureException("批量插入审批明细数据失败");
                        }
                    }
                }
            } else {
                String values;
                if (StringUtils.isNotBlank(value)) {
                    ApprovalAttr attr = new ApprovalAttr();
                    attr.setId(IDUtils.uuid());
                    attr.setApprovalId(approval.getId());
                    attr.setAttrName(name);
                    attr.setAttrType(type);
                    val = obj.getString("value");
                    values = obj.getString("values");
                    if (type == ApproConstants.TIME_INTERVAL_TYPE_5 && values != null) {
                        attr.setAttrValue(EmojiFilterUtils.filterEmoji(val) + "," + values);
                    } else {
                        attr.setAttrValue(EmojiFilterUtils.filterEmoji(val));
                    }
                    attrSet.add(attr);
                }
            }

        }
        attrSet.addAll(contentSet);
        List<ApprovalAttr> attrsList = new ArrayList<>(attrSet);
        boolean isInserted = approvalAttrService.insertBatch(attrsList);
        if (!isInserted) {
            throw new InsertMessageFailureException("批量插入审批信息数据失败");
        }
        // 保存接收人
        saveApprovalUser(sendUserIds, approval);
        // 保存抄送人
        saveCopyUser(sendCopyIds, approval);

        //推送审批
        approvalPushTask.init(approval.getId(), companyId, memberId).run();

        return isInserted;
    }

    @Override
    public PageWrapper<ApprovalVO> page(DataParam dataParam) throws Exception {
        PageWrapper<Approval> page = this.getPage(dataParam);
        List<Approval> approvalList = page.getRecords();
        if (CollectionUtils.isEmpty(approvalList)) {
            return null;
        }
        List<String> approvaIds = approvalList.stream().map(Approval::getId).collect(Collectors.toList());
        List<ApprovalProcess> approvalProcessList = approvalProcessService.selectList(new EntityWrapper<ApprovalProcess>().in("approval_id", approvaIds).and("is_delete=0"));

        if (CollectionUtils.isEmpty(approvalProcessList)) {
            throw new MessageNotExitException("审批流程信息不存在");
        }

        Set<String> userIdSet = approvalProcessList.stream().map(ApprovalProcess::getUserId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(userIdSet)) {
            throw new MessageNotExitException("审批人信息不存在");
        }

        List<String> userIds = new ArrayList<>(userIdSet.size());
        userIds.addAll(userIdSet);
        List<ApprovalUser> users = approvalUserService.selectList(Condition.create().in("id", userIds));
        if (CollectionUtils.isEmpty(users)) {
            throw new MessageNotExitException("获取审批人信息不存在");
        }

        // key = id, value = nick
        Map<String, String> userMap = users.stream().collect(Collectors.toMap(ApprovalUser::getId, ApprovalUser::getName));

        List<ApprovalVO> records = new ArrayList<>(approvalList.size());
        approvalList.forEach(approval -> {
            ApprovalVO approvalVO = new ApprovalVO(approval);
            List<String> uIds = approvalProcessList.parallelStream().filter(process -> approval.getId().equals(process.getApprovalId()))
                    .sorted(comparing(ApprovalProcess::getSeq)).map(ApprovalProcess::getUserId).collect(Collectors.toList());
            List<String> nicks = new ArrayList<>(uIds.size());
            uIds.forEach(uid -> nicks.add(userMap.get(uid)));
            String approver = StringUtils.join(nicks, "|");
            approvalVO.setApprover(approver);
            records.add(approvalVO);
        });
        PageWrapper<ApprovalVO> result = new PageWrapper<>();
        result.setCurrent(dataParam.getCurrentPage());
        result.setSize(dataParam.getPageSize());
        result.setTotal(page.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String approvalId) throws Exception {
        boolean flag = false;

        // 先删除有父主键的审批属性数据
        approvalAttrService.delete(Condition.create().where("approval_id={0}", approvalId).and("attr_parent is not null").or("attr_parent != ''"));
        // 再删除所有的审批属性的数据
        approvalAttrService.delete(Condition.create().where("approval_id={0}", approvalId));

        // 删除抄送数据
        copySService.delete(Condition.create().where("approval_id={0}", approvalId));

        // 删除审批流程中数据
        approvalProcessService.delete(Condition.create().where("approval_id={0}", approvalId));

        // 删除审批结果数据
        flag = approvalService.delete(Condition.create().where("id={0}", approvalId));
        if (!flag) {
            throw new DeleteMessageFailureException("审批结果数据删除异常或已经被删除");
        }
        return true;
    }

    @Override
    public BaseExModel createApprovalExcel(DataParam dataParam) throws Exception {
        String orgId = dataParam.getCompanyId();
        String userId = dataParam.getMemberId();
        String modelId = dataParam.getModelId();

        List<ApprovalExcelVO> exportData = getExportData(dataParam);
        ApprovalExModel approvalExModel = new ApprovalExModel();
        // 从redis缓存中获取approvalExData
        ApprovalExData approvalExData = approvalRedisService.get(orgId + userId);
        Map<String, List<ApprovalTemplVO>> temMap = approvalExData.getTemMap();

        List<ModelItem> modelItemList = modelItemMapper.selectAll(orgId);

        // 工作表名称处理
        List<String> modelIdList = exportData.parallelStream().map(ApprovalExcelVO::getModelId).collect(Collectors.toSet()).stream().collect(Collectors.toList());
        if (null != modelId) {
            modelIdList.add(modelId);
        }
        List<ModelL> modelLS;
        if (CollectionUtils.isNotEmpty(modelIdList)) {
            modelLS = modelService.selectList(Condition.create().in("id", modelIdList));
        } else {
            modelLS = new ArrayList<>();
        }
        // 文件名
        String currentTime = DateUtil.DateToString(new Date(), DateStyle.YYYYMMDDHHMMSS);
        StringBuilder fileName = new StringBuilder().append(currentTime).append(ApprovalExConsts.SEPARATOR_POINT).append(ApprovalExConsts.Type_xls);
        List<ExcelModel> excelModelList = new ArrayList<>();
        String tableHead = "报表生成日期：" + DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS);
        if (!modelLS.isEmpty()) {
            for (ModelL modelL : modelLS) {
                int length = modelL.getModelVersion() + 1;
                for (int i = 1; i < length; i++) {
                    final Integer modelVersion = i;
                    modelL.setModelVersion(i);
                    ExcelModel excelModel = new ExcelModel();
                    // 注入工作表名称
                    excelModel.setSheetName(modelL.getModelName() + "(" + i + ")");

                    // 注入文件名
                    excelModel.setFileName(fileName.toString());

                    // 注入表头l
                    String statisticDate = "统计日期：";
                    if (null != dataParam.getCreateTimeStart() && null != dataParam.getCreateTimeEnd()) {
                        statisticDate = statisticDate + dataParam.getCreateTimeStart() + " —— " + dataParam.getCreateTimeEnd() + "       ";
                    } else {
                        statisticDate = getExportTime(orgId, statisticDate);
                    }
                    excelModel.setTableHeader(statisticDate + tableHead);
                    if (!exportData.isEmpty() && exportData.get(0).getModelVersion() == null) {
                        List<ModelItem> collect = modelItemList.stream().filter(modelItem -> modelItem.getModelId().equals(modelL.getId())).collect(Collectors.toSet()).stream().collect(Collectors.toList());
                        List<ApprovalTemplVO> approvalTemplVOList = new ArrayList<>();
                        collect.forEach(modelItem -> {
                            ApprovalTemplVO approvalTemplVO = new ApprovalTemplVO();
                            approvalTemplVO.setCKey(modelItem.getItemLabel());
                            approvalTemplVOList.add(approvalTemplVO);
                        });
                        List<ApprovalTemplVO> collect1 = approvalTemplVOList.stream().sorted(comparing(ApprovalTemplVO::getCKey))
                                .collect(Collectors.toSet()).stream().collect(Collectors.toList());
                        excelModel.setTitles(collect1);
                        // 注入审批数据
                        List<ApprovalExcelVO> approvalExcelVOS = exportData.stream()
                                .filter(approvalExcelVO -> approvalExcelVO.getModelId().equals(modelL.getId()))
                                .collect(Collectors.toList());
                        excelModel.setApprovalList(approvalExcelVOS);
                        excelModelList.add(excelModel);

                    } else {
                        for (Map.Entry<String, List<ApprovalTemplVO>> entry : temMap.entrySet()) {
                            String key = modelL.getId() + "-" + modelVersion;
                            if (key.equals(entry.getKey())) {
                                // 注入数据项名称
                                excelModel.setTitles(entry.getValue());
                                // 注入审批数据
                                List<ApprovalExcelVO> approvalExcelVOS = exportData.stream()
                                        .filter(approvalExcelVO -> approvalExcelVO.getModelId().equals(modelL.getId()))
                                        .filter(approvalExcelVO -> modelVersion.equals(approvalExcelVO.getModelVersion()))
                                        .collect(Collectors.toList());
                                excelModel.setApprovalList(approvalExcelVOS);
                                excelModelList.add(excelModel);
                            }
                        }
                    }
                }
            }
        } else {
            ExcelModel excelModel = new ExcelModel();
            excelModel.setFileName(fileName.toString());
            excelModel.setSheetName("Sheet1");
            List<ApprovalTemplVO> approvalTemplVOList = new ArrayList<>();
            excelModel.setTitles(approvalTemplVOList);
            // 注入表头l
            String statisticDate = "统计日期：";
            if (null != dataParam.getCreateTimeStart() && null != dataParam.getCreateTimeEnd()) {
                statisticDate = statisticDate + dataParam.getCreateTimeStart() + " —— " + dataParam.getCreateTimeEnd() + "       ";
            } else {
                statisticDate = getExportTime(orgId, statisticDate);
            }

            excelModel.setTableHeader(statisticDate + tableHead);
            excelModelList.add(excelModel);
        }
        approvalExModel.setExcelModelList(excelModelList);
        approvalExModel.setFileName(fileName.toString());

        //  保存导出记录
        saveExportLog(orgId, userId, modelId, fileName.toString(), exportData);
        return approvalExModel;
    }

    private String getExportTime(String orgId, String statisticDate) {
        CompanyDTO companyDTO = (CompanyDTO) redisTemplate.getTemple().opsForHash().get(ApproConstants.BOTONG_ORG, orgId);
        String orgCreateTime = "";
        if (companyDTO != null) {
            orgCreateTime = DateUtil.convert(companyDTO.getStartTime());
        }
        statisticDate = statisticDate + DateUtil.StringToString(orgCreateTime, DateStyle.YYYY_MM_DD) + " —— " + DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD) + "       ";
        return statisticDate;
    }

    /**
     * 获取需要导出的审批数据
     *
     * @param dataParam 参数
     * @return
     * @throws Exception
     */
    private List<ApprovalExcelVO> getExportData(DataParam dataParam) throws Exception {
        String orgId = dataParam.getCompanyId();
        String userId = dataParam.getMemberId();
        approvalRedisService.remove(orgId + userId);
        List<Approval> approvalList = this.getList(dataParam);
        List<ApprovalExcelVO> excelVOList = new ArrayList<>();
        Map<String, List<ApprovalTemplVO>> tmpMap = new HashMap<>(1);
        // 获取审批人
        List<String> approvalIds = approvalList.stream().map(Approval::getId).collect(Collectors.toList());
        List<ApprovalProcess> approvalProcessList = approvalProcessService.selectList(new EntityWrapper<ApprovalProcess>().in("approval_id", approvalIds));
        if (CollectionUtils.isEmpty(approvalProcessList)) {
            logger.error("审批流程信息不存在");
            throw new MessageNotExitException("审批流程信息不存在");
        }
        Set<String> userIdSet = approvalProcessList.stream().map(ApprovalProcess::getUserId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIdSet)) {
            logger.error("审批人信息不存在");
            throw new MessageNotExitException("审批人信息不存在");
        }
        List<String> userIds = new ArrayList<>(userIdSet.size());
        userIds.addAll(userIdSet);
        List<ApprovalUser> users = approvalUserService.selectList(Condition.create().in("id", userIds));
        if (CollectionUtils.isEmpty(users)) {
            logger.error("获取审批人信息不存在");
            throw new MessageNotExitException("获取审批人信息不存在");
        }
        // 封装审批人主键与昵称 （key = id, value = nick）
        Map<String, String> userMap = users.stream().collect(Collectors.toMap(ApprovalUser::getId, ApprovalUser::getName));

        // 获取该企业下所有modelItem
        List<ModelItem> modelItems = modelItemMapper.selectAll(orgId);
        // 获取该企业下所有的审批属性信息
        List<ApprovalAttr> approvalAttrList = approvalAttrMapper.selectAttrByOrgId(orgId);
        // 从redis缓存中批量获取用户信息
        List<String> userIdList = approvalList.stream().map(Approval::getUserId).collect(Collectors.toList());
        List<ApprovalUser> userList = new ArrayList<>();
        if (userIdList != null && !userIdList.isEmpty()) {
            userList = approvalUserService.selectList(Condition.create().in("id", userIds));
        }
        // 查询编辑审批表单后填写的所有审批信息数据
        List<ApproveAttributeVO> approveAttrList = approvalAttrMapper.selectAttrListByOrgId(orgId);
        for (Approval approval : approvalList) {
            // 审批状态
            String status = "";
            // 审批结果
            String result = "";
            // 耗时
            String timeConsuming = "";
            ApprovalExcelVO excelVO = new ApprovalExcelVO();
            // 模型主键
            if (approval != null) {
                excelVO.setModelId(approval.getModelId());
                // 审批标题
                excelVO.setTitle(approval.getTitle());
                // 审批状态
                if (approval.getState() == 0) {
                    status = "审批中";
                } else if (approval.getState() == 1) {
                    status = "审批完成";
                } else if (approval.getState() == 2) {
                    status = "已撤回";
                }
                excelVO.setState(status);
                // 审批结果
                if (null != approval.getResult() && approval.getResult() == 1) {
                    result = "同意";
                } else if (null != approval.getResult() && approval.getResult() == 2) {
                    result = "拒绝";
                } else if (null != approval.getResult() && approval.getResult() == 4) {
                    result = "已撤销";
                }
                excelVO.setResult(result);
                // 审批发起时间
                excelVO.setCreateTime(approval.getCreateTime() != null ? DateUtil.convert(approval.getCreateTime()) : null);
                // 审批结束时间
                excelVO.setFinishTime(approval.getFinishTime() != null ? DateUtil.convert(approval.getFinishTime()) : null);
                List<ApprovalUser> userVO1 = userList.parallelStream().filter(userVO -> userVO.getId().equals(approval.getUserId())).collect(Collectors.toList());
                if (!userVO1.isEmpty()) {
                    // 发起人姓名
                    excelVO.setUserName(userVO1.get(0).getName());
                    // 发起人部门
                    excelVO.setDeptName(userVO1.get(0).getDeptName());
                }
                // 审批人
                List<String> uIds = approvalProcessList.parallelStream().filter(process -> approval.getId().equals(process.getApprovalId()))
                        .sorted(comparing(ApprovalProcess::getSeq)).map(ApprovalProcess::getUserId).collect(Collectors.toList());
                List<String> nicks = new ArrayList<>(uIds.size());
                uIds.forEach(uid -> nicks.add(userMap.get(uid)));
                String approver = StringUtils.join(nicks, "|");
                excelVO.setApprovalName(approver);

                // 耗时
                if (null != approval.getFinishTime() && null != approval.getCreateTime()) {
                    long time = approval.getFinishTime() - approval.getCreateTime();
                    timeConsuming = getTime(time);
                    excelVO.setTimeConsuming(timeConsuming);
                } else {
                    excelVO.setTimeConsuming(timeConsuming);
                }

                List<ModelItem> items = modelItems.parallelStream()
                        .filter(modelItem -> modelItem.getModelId().equals(approval.getModelId())).collect(Collectors.toList());
                List<ApprovalAttr> attrs = approvalAttrList.parallelStream()
                        .filter(approvalAttr -> approvalAttr.getApprovalId().equals(approval.getId())).collect(Collectors.toList());

                List<ApproveAttributeVO> approveAttrs = approveAttrList.parallelStream()
                        .filter(approveAttr -> approveAttr.getApprovalId().equals(approval.getId())).collect(Collectors.toList());
                // 其他字段名和字段值
                Map<String, Object> map = approvalDataByType(orgId, userId, approval, items, attrs, approveAttrs);
                List<AttrValueVO> attrValueList = getAttrValue(map);
                excelVO.setListValue(attrValueList);
                // 版本
                excelVO.setModelVersion(approval.getModelVersion());
                // 添加到集合
                excelVOList.add(excelVO);
            }
            ApprovalExData approvalExData = approvalRedisService.get(orgId + userId);
            tmpMap.put(approval.getModelId() + "-" + approval.getModelVersion(), approvalExData.getApprovalTempVOList());
        }
        ApprovalExData exData = new ApprovalExData(tmpMap);
        approvalRedisService.put(orgId + userId, exData, 30 * 60);
        return excelVOList;

    }

    /**
     * 封装审批数据
     *
     * @param approval     审批对象
     * @param modelItems   审批模型项
     * @param attrList     所有审批信息数据
     * @param approveAttrs 编辑表单后填写的审批信息数据
     * @return
     */
    private Map<String, Object> approvalDataByType(String orgId, String userId, Approval approval, List<ModelItem> modelItems, List<ApprovalAttr> attrList, List<ApproveAttributeVO> approveAttrs) {
        approvalRedisService.remove(orgId + userId);
        List<ApprovalTemplVO> approvalTemplVOS = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(16);
        if (null != approval.getModelVersion()) {
            for (ApproveAttributeVO attr : approveAttrs) {
                ApprovalTemplVO approvalTemplVO = new ApprovalTemplVO();
                if (attr.getAttrParent() != null) {
                    continue;
                }

                Integer type = attr.getAttrType();
                if (type == null) {
                    continue;
                }
                // 当item是明细时的情况
                if (type == 7) {
                    ApproveAttrVO attrVo = new ApproveAttrVO(attr);
                    approvalTemplVO.setCKey(attrVo.getLabel());
                    Map<Integer, List<ApproveAttrVO>> mingXiMap = new HashMap<>(1);
                    for (ApproveAttributeVO childAttr : approveAttrs) {
                        if (childAttr.getAttrParent() == null) {
                            continue;
                        }

                        if (attr.getId().equals(childAttr.getAttrParent())) {
                            Integer num = childAttr.getAttrNum();
                            if (num == null) {
                                continue;
                            }

                            List<ApproveAttrVO> childAttrs = mingXiMap.get(num);

                            if (CollectionUtils.isEmpty(childAttrs)) {
                                childAttrs = new ArrayList<>();
                            }
                            childAttrs.add(new ApproveAttrVO(childAttr));
                            mingXiMap.put(num, childAttrs);
                        }
                    }

                    if (MapUtils.isNotEmpty(mingXiMap)) {
                        List<ApproveRowVO> details = new ArrayList<>(mingXiMap.size());
                        for (Map.Entry<Integer, List<ApproveAttrVO>> entry : mingXiMap.entrySet()) {
                            details.add(new ApproveRowVO(entry.getKey(), entry.getValue()));
                        }
                        attrVo.setContents(details);
                        map.put(attrVo.getLabel(), attrVo.getContents());
                    } else {
                        map.put(attrVo.getLabel(), attrVo.getValue());
                    }
                } else if (type == ApproConstants.PICTURE_TYPE_10) {
                    ApproveAttrVO attrVo = new ApproveAttrVO(attr);
                    map.put(attrVo.getLabel(), attrVo.getImages());
                    approvalTemplVO.setCKey(attrVo.getLabel());
                } else if (type == ApproConstants.ENCLOSURE_TYPE_11) {
                    ApproveAttrVO attrVo = new ApproveAttrVO(attr);
                    map.put(attrVo.getLabel(), attrVo.getFiles());
                    approvalTemplVO.setCKey(attrVo.getLabel());
                } else if (type == ApproConstants.TIME_INTERVAL_TYPE_5) {
                    ApproveAttrVO attrVo = new ApproveAttrVO(attr);
                    map.put(attrVo.getLabel(), attrVo.getValue());
                    map.put(attrVo.getLabels(), attrVo.getValues());
                    ApprovalTemplVO approvalTemplVO1 = new ApprovalTemplVO();
                    approvalTemplVO.setCKey(attrVo.getLabel());
                    approvalTemplVO1.setCKey(attrVo.getLabels());
                    approvalTemplVOS.add(approvalTemplVO1);

                } else {
                    map.put(attr.getAttrLabel(), attr.getAttrValue());
                    approvalTemplVO.setCKey((attr.getAttrLabel()));
                }
                approvalTemplVOS.add(approvalTemplVO);
            }
        } else {
            modelItems.forEach(modelItem -> {
                String label = modelItem.getItemLabel();
                String field = modelItem.getField();
                List<ApprovalAttr> approvalAttrList = attrList.parallelStream()
                        .filter(approvalAttr -> approvalAttr.getAttrName().equals(field))
                        .collect(Collectors.toList());
                for (ApprovalAttr attr : approvalAttrList) {
                    ApprovalTemplVO approvalTemplVO = new ApprovalTemplVO();
                    if (attr != null && StringUtils.isNotBlank(attr.getAttrValue())) {
                        if ("图片".equals(label)) {
                            String url = "";
                            String attrValue = attr.getAttrValue();
                            List<ImageVO> imageVOS = JSONArray.parseArray(attrValue, ImageVO.class);
                            for (ImageVO imageVO : imageVOS) {
                                url = new StringBuilder(url) + imageVO.getUrl() + " \r\n";
                            }
                            map.put(label, url);
                            approvalTemplVO.setCKey(label);
                        } else if ("附件".equals(label)) {
                            String enclosure = "";
                            String attrValue = attr.getAttrValue();
                            List<FileVO> fileVOS = JSONArray.parseArray(attrValue, FileVO.class);
                            for (FileVO fileVO : fileVOS) {
                                enclosure = new StringBuilder(enclosure) + fileVO.getName() != null ? fileVO.getName() : "无" + " | "
                                        + fileVO.getSize() != null ? fileVO.getSize() : "无" + " | " + fileVO.getUrl() + " \r\n";
                            }
                            map.put(label, enclosure);
                            approvalTemplVO.setCKey(label);
                        } else {
                            map.put(label, attr.getAttrValue());
                            approvalTemplVO.setCKey(label);
                        }
                    } else {
                        map.put(label, "");
                        approvalTemplVO.setCKey(label);
                    }
                    approvalTemplVOS.add(approvalTemplVO);
                }
            });
        }
        ApprovalExData approvalExData = new ApprovalExData(approvalTemplVOS);
        approvalRedisService.put(orgId + userId, approvalExData, 30 * 60);
        return map;
    }

    /**
     * 获取审批属性名和属性值
     *
     * @param map
     * @return
     */
    private List<AttrValueVO> getAttrValue(Map<String, Object> map) {
        List<AttrValueVO> attrValueList = new ArrayList<>();
        String url = "";
        String enclosure = "";
        String detail = "";
        String val = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            AttrValueVO attrValue = new AttrValueVO();
            attrValue.setCkey(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof List) {
                for (Object obj : (List) value) {
                    if (obj instanceof ImageVO) {
                        ImageVO imageVO = (ImageVO) obj;
                        url = new StringBuilder(url) + imageVO.getUrl() + " \r\n";
                        attrValue.setAttrVal(url);
                    } else if (obj instanceof FileVO) {
                        FileVO fileVO = (FileVO) obj;
                        enclosure = new StringBuilder(enclosure) + (fileVO.getName() != null ? fileVO.getName() : "") + " | "
                                + (fileVO.getSize() != null ? fileVO.getSize() : "") + " | " + fileVO.getUrl() + " \r\n";
                        attrValue.setAttrVal(enclosure);
                    } else if (obj instanceof ApproveRowVO) {
                        ApproveRowVO approveRowVO = (ApproveRowVO) obj;
                        List<ApproveAttrVO> attrs = approveRowVO.getAttrs();
                        for (ApproveAttrVO attr : attrs) {
                            val = new StringBuilder(val) + attr.getLabel() + ":" + attr.getValue() + " \r\n";
                        }
                        detail = new StringBuilder(detail) + "(" + String.valueOf(approveRowVO.getNum()) + ")\r\n" + val + " \r\n";
                        attrValue.setAttrVal(detail);
                        val = "";
                    }
                }
            } else {
                attrValue.setAttrVal(value != null ? value.toString() : "");
            }
            attrValueList.add(attrValue);
        }
        return attrValueList;
    }

    /**
     * 保存导出记录
     *
     * @param orgId      企业主键
     * @param userId     用户主键
     * @param modelId    模型主键
     * @param fileName   文件名称
     * @param exportData 导出数据
     * @return
     */
    private boolean saveExportLog(String orgId, String userId, String modelId, String fileName, List<ApprovalExcelVO> exportData) throws Exception {

        if (null == orgId) {
            throw new ParameterErrorException("该企业不存在");
        }
        if (null == userId) {
            throw new ParameterErrorException("该用户不存在");
        }
        String modelIds;
        Set<String> set = new HashSet<>();
        ExportLog exportLog = new ExportLog();
        if (exportData.isEmpty()) {
            exportLog.setModelId("");
        }
        exportData.forEach(approvalExcelVO -> set.add(approvalExcelVO.getModelId()));
        if (StringUtils.isBlank(modelId.toString()) && set.size() > 1) {
            modelIds = "";
            for (String m : set) {
//                modelIds = new StringBuilder().append(modelIds) + m + ",";
            }
            exportLog.setModelId(modelIds);
        } else if (StringUtils.isBlank(modelId.toString()) && set.size() == 1) {
            Object[] obj = set.stream().toArray();
            exportLog.setModelId((String) obj[0]);
        } else {
            exportLog.setModelId(modelId.toString());
        }
        exportLog.setFileName(fileName);
        exportLog.setId(IDUtils.uuid());
        exportLog.setCreateTime(System.currentTimeMillis());
        exportLog.setOrgId(orgId);
        exportLog.setUserId(userId);
        boolean insert = exportLogService.insert(exportLog);

        if (!insert) {
            throw new InsertMessageFailureException("保存导出记录失败");
        }
        return true;
    }

    private Wrapper<Approval> getWrapper(DataParam dataParam) throws Exception {
        Wrapper<Approval> wrapper = new EntityWrapper<>();
        if (StringUtils.isBlank(dataParam.getCompanyId())) {
            throw new ParameterErrorException("主键不存在");
        }
        wrapper.eq("org_id", dataParam.getCompanyId());
        if (StringUtils.isNotBlank(dataParam.getModelId())) {
            wrapper.in("model_id", dataParam.getModelId());
        }
        if (dataParam.getState() != null) {
            wrapper.eq("state", dataParam.getState());
        }
        if (StringUtils.isNotBlank(dataParam.getTitle())) {
            wrapper.like("title", dataParam.getTitle());
        }
        if (null != dataParam.getCreateTimeStart()) {
            wrapper.ge("from_unixtime(create_time/1000, '%Y%m%d')", convertDate(dataParam.getCreateTimeStart()));
        }

        if (null != dataParam.getCreateTimeEnd()) {

            wrapper.le("from_unixtime(create_time/1000, '%Y%m%d')", convertDate(dataParam.getCreateTimeEnd()));
        }

        if (null != dataParam.getFinishTimeStart()) {
            wrapper.ge("from_unixtime(finish_time/1000, '%Y%m%d')", convertDate(dataParam.getFinishTimeStart()));
        }

        if (null != dataParam.getFinishTimeEnd()) {
            wrapper.le("from_unixtime(finish_time/1000, '%Y%m%d')", convertDate(dataParam.getFinishTimeEnd()));
        }
        wrapper.orderBy("create_time", false);
        return wrapper;
    }

    private List<Approval> getList(DataParam dataParam) throws Exception {
        return this.selectList(getWrapper(dataParam).orderBy("model_id", true));
    }

    private PageWrapper<Approval> getPage(DataParam dataParam) throws Exception {
        com.baomidou.mybatisplus.plugins.Page page = new com.baomidou.mybatisplus.plugins.Page(dataParam.getCurrentPage(), dataParam.getPageSize());
        com.baomidou.mybatisplus.plugins.Page page1 = this.selectPage(page, getWrapper(dataParam));
        PageWrapper<Approval> pageWrapper = BeanUtils.mapPage(page1, Approval.class);
        return pageWrapper;
    }

    /**
     * 保存审批人信息
     *
     * @param sendUserIds 审批人主键
     * @param approval    审批对象
     * @return
     */
    private boolean saveApprovalUser(String sendUserIds, Approval approval) throws Exception {
        boolean flag = false;
        if (sendUserIds != null) {
            // 去除可能的重复数据
            Set<String> strSet = new HashSet<>();
            String[] userIds = sendUserIds.split(",");
            for (String str : userIds) {
                strSet.add(str);
            }
            String[] ids = new String[strSet.size()];
            int n = 0;
            for (String string : strSet) {
                ids[n] = string;
                n++;
            }
            List<ApprovalUser> users = approvalUserService.selectList(Condition.create().in("id", ids));
            Set<ApprovalProcess> approvalProcesses = new HashSet<>();
            for (int j = 0; j < userIds.length; j++) {
                for (ApprovalUser user : users) {
                    if (userIds[j].equals(user.getId())) {
                        ApprovalProcess approvalProcess = new ApprovalProcess();
                        approvalProcess.setId(IDUtils.uuid());
                        approvalProcess.setApprovalId(String.valueOf(approval.getId()));
                        approvalProcess.setProcessTime(System.currentTimeMillis());
                        approvalProcess.setProcessState(0);
                        approvalProcess.setSeq(j + 1);
                        approvalProcess.setUserId(String.valueOf(user.getId()));
                        approvalProcesses.add(approvalProcess);
                    }
                }
            }
            List<ApprovalProcess> approvalProcesses1 = new ArrayList<>(approvalProcesses);
            if (!approvalProcesses1.isEmpty()) {
                flag = approvalProcessService.insertBatch(approvalProcesses1);
                if (!flag) {
                    throw new InsertMessageFailureException("批量保存审批人失败");
                }
            }
        }
        return flag;
    }

    /**
     * 保存抄送人
     *
     * @param sendCopyIds 抄送人主键
     * @param approval    审批对象
     * @return
     */
    private boolean saveCopyUser(String sendCopyIds, Approval approval) throws Exception {
        boolean flag = false;
        if (StringUtils.isNotBlank(sendCopyIds)) {
            // 去除可能的重复数据
            Set<String> strSet = new HashSet<>();
            String[] userIds = sendCopyIds.split(",");
            for (String str : userIds) {
                strSet.add(str);
            }
            String[] ids = new String[strSet.size()];
            int n = 0;
            for (String string : strSet) {
                ids[n] = string;
                n++;
            }
            List<ApprovalUser> users = approvalUserService.selectList(Condition.create().in("id", ids));
            Set<Copys> copies = new HashSet<>();
            for (ApprovalUser user : users) {
                Copys copys1 = new Copys();
                copys1.setApprovalId(approval.getId());
                copys1.setUserId(user.getId());
                copys1.setId(IDUtils.uuid());
                copys1.setCopySType(0);
                copys1.setCreateTime(System.currentTimeMillis());
                copies.add(copys1);
            }
            if (!copies.isEmpty()) {
                flag = copySService.insertBatch(new ArrayList<>(copies));
                if (!flag) {
                    throw new InsertMessageFailureException("批量保存抄送人失败");
                }
            }
        }
        return flag;
    }


    private String convertDate(Long time) {
        String date = com.yunjing.mommon.utils.DateUtil.convert(time);
        return date.substring(0, date.indexOf("")).replaceAll("-", "");
    }

    /**
     * 将给定的时间毫秒值转换为格式为时分秒（比如：1小时30分15秒）
     *
     * @param time 时间毫秒值
     * @return
     */
    private String getTime(long time) {
        String str = "";
        time = time / 1000;
        int s = (int) (time % 60);
        int m = (int) (time / 60 % 60);
        int h = (int) (time / 3600 % 24);
        int d = (int) time / (3600 * 24);
        str = d + "天" + h + "小时" + m + "分" + s + "秒";
        return str;
    }
}
