package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.cache.ApprovalRedisService;
import com.yunjing.approval.dao.cache.OrgReadisService;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.*;
import com.yunjing.approval.excel.*;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.processor.task.async.ApprovalPushTask;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.approval.util.DateUtil;
import com.yunjing.approval.util.EmojiFilterUtils;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * @author roc
 * @date 2018/1/15
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ApprovalServiceImpl extends BaseServiceImpl<ApprovalMapper, Approval> implements IApprovalService {

    private final Log logger = LogFactory.getLog(ApprovalServiceImpl.class);
    @Autowired
    private IApprovalProcessService approvalProcessService;
    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;
    @Autowired
    private IApprovalAttrService approvalAttrService;
    @Autowired
    private ICopySService copySService;
    @Autowired
    private CopySMapper copySMapper;
    @Autowired
    private IApprovalService approvalService;
    @Autowired
    private UserRedisService userRedisService;
    @Autowired
    private IExportLogService exportLogService;

    @Autowired
    private ModelItemMapper modelItemMapper;
    @Autowired
    private IModelService modelService;
    @Autowired
    private OrgReadisService orgReadisService;
    @Autowired
    private ApprovalAttrMapper approvalAttrMapper;
    @Autowired
    private ApprovalRedisService approvalRedisService;
    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private ApprovalPushTask approvalPushTask;

    @Override
    public boolean submit(Long oid, Long uid, Long modelId, String jsonData, String sendUserIds, String sendCopyIds) throws Exception {
        ModelL modelL = modelService.selectById(modelId);
        Approval approval = new Approval();
        approval.setId(IDUtils.getID());
        approval.setModelId(modelId);
        approval.setOrgId(oid);
        approval.setUserId(uid);
        approval.setCreateTime(DateUtil.getCurrentTime().getTime());
        approval.setState(0);
        approval.setModelVersion(modelL.getModelVersion());
        // 处理审批标题
        String title = "不存在的";
        if (uid == null) {
            title += "用户";
        } else {
            ApprovalUser user = approvalUserService.selectOne(Condition.create().where("id={0}", uid));
            String nick = user.getName();
            String name = modelL.getModelName();

            if (StringUtils.isBlank(nick)) {
                title += "用户昵称";
            } else if (StringUtils.isBlank(name)) {
                title += "模型名称";
            } else {
                title = user.getName() + "的" + modelL.getModelName();
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
        JSONArray jsonArray = JSON.parseArray(jsonData);
        Iterator<Object> it = jsonArray.iterator();
        while (it.hasNext()) {
            ApprovalAttr attr = new ApprovalAttr();
            attr.setId(IDUtils.getID());
            attr.setApprovalId(approval.getId());
            JSONObject obj = (JSONObject) it.next();
            int type = obj.getIntValue("type");
            String name = obj.getString("field");
            attr.setAttrName(name);
            attr.setAttrType(type);
            String value;
            String values;
            // 类型是10-图片, 11-附件的情况
            if (type == ApproConstants.PICTURE_TYPE_10 || type == ApproConstants.ENCLOSURE_TYPE_11) {
                JSONArray array = obj.getJSONArray("value");
                if (array != null && array.size() > 0) {
                    value = array.toJSONString();
                    attr.setAttrValue(EmojiFilterUtils.filterEmoji(value));
                }
            } else {
                value = obj.getString("value");
                values = obj.getString("values");
                if (StringUtils.isNotBlank(String.valueOf(values))) {
                    attr.setAttrValue(EmojiFilterUtils.filterEmoji(value) + "," + values);
                } else {
                    attr.setAttrValue(EmojiFilterUtils.filterEmoji(value));
                }
                if (type == ApproConstants.DETAILED_TYPE_7) {
                    // 明细类型
                    JSONArray array = obj.getJSONArray("content");
                    Iterator<Object> content = array.iterator();
                    while (content.hasNext()){

                        ApprovalAttr attr1 = new ApprovalAttr();
                        attr1.setId(IDUtils.getID());
                        attr1.setApprovalId(approval.getId());
                        JSONObject contents = (JSONObject) content.next();
                        JSONArray array1 = contents.getJSONArray("modelItems");
                        int types = contents.getIntValue("type");
                        String field = contents.getString("field");
                        attr1.setAttrType(types);
                        attr1.setAttrName(field);
                        contentSet.add(attr1);
                        Iterator<Object> modelItems = array1.iterator();
                        Set<ApprovalAttr> attrs = new HashSet<>();
                        while (modelItems.hasNext()) {
                            ApprovalAttr entity = new ApprovalAttr();
                            entity.setId(IDUtils.getID());
                            entity.setApprovalId(approval.getId());
                            entity.setAttrParent(attr1.getId());
                            JSONObject detail = (JSONObject) modelItems.next();

                            int detailType = detail.getIntValue("type");
                            String detailName = detail.getString("field");
                            entity.setAttrName(detailName);
                            entity.setAttrType(detailType);
                            String detailValue = "";
                            String detailValues = "";
                            // 明细中类型是10-图片, 11-附件的情况
                            if (detailType == ApproConstants.PICTURE_TYPE_10 || detailType == ApproConstants.ENCLOSURE_TYPE_11) {
                                JSONArray detailArray = detail.getJSONArray("value");
                                detailValue = detailArray.toJSONString();
                                entity.setAttrValue(EmojiFilterUtils.filterEmoji(detailValue));
                            } else if (detailType == ApproConstants.TIME_INTERVAL_TYPE_5){
                                detailValue = detail.getString("value");
                                detailValues = detail.getString("values");
                                entity.setAttrValue(EmojiFilterUtils.filterEmoji(detailValue) + "," + detailValues);
                            }else {
                                entity.setAttrValue(EmojiFilterUtils.filterEmoji(detailValue));
                            }

                            int detailNum = detail.getIntValue("num");
                            entity.setAttrNum(detailNum);

                            attrs.add(entity);
                        }
                        List<ApprovalAttr> attrList = new ArrayList<>(attrs);
                        boolean b = approvalAttrService.insertBatch(attrList);
                        if (!b) {
                            throw new InsertMessageFailureException("批量插入审批明细数据失败");
                        }
                    }
                }
            }
            attrSet.add(attr);
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
        approvalPushTask.init(approval.getId(), oid, uid).run();

        return isInserted;
    }

    @Override
    public ApprovalPageVO page(Page<Approval> page, Long oid, Long mid, Integer state, String title, String createTimeStart, String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws Exception {
        page = this.getPage(page, oid, mid, state, title, createTimeStart, createTimeEnd, finishTimeStart, finishTimeEnd);
        List<Approval> approvalList = page.getRecords();
        if (CollectionUtils.isEmpty(approvalList)) {
            return null;
        }

        ApprovalPageVO result = new ApprovalPageVO(page);

        List<Long> approvaIds = approvalList.stream().map(Approval::getId).collect(Collectors.toList());
        List<ApprovalProcess> approvalProcessList = approvalProcessService.selectList(new EntityWrapper<ApprovalProcess>().in("approval_id", approvaIds));

        if (CollectionUtils.isEmpty(approvalProcessList)) {
            throw new BaseException("审批流程信息不存在");
        }

        Set<Long> userIdSet = approvalProcessList.stream().map(ApprovalProcess::getUserId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(userIdSet)) {
            throw new BaseException("审批人信息不存在");
        }

        List<Long> userIds = new ArrayList<>(userIdSet.size());
        userIds.addAll(userIdSet);
        List<ApprovalUser> users = approvalUserService.selectBatchIds(userIds);

        if (CollectionUtils.isEmpty(users)) {
            throw new BaseException("获取审批人信息不存在");
        }

        // key = id, value = nick
        Map<Long, String> userMap = users.stream().collect(Collectors.toMap(ApprovalUser::getId, ApprovalUser::getName));

        List<ApprovalVO> records = new ArrayList<>(approvalList.size());
        approvalList.forEach(approval -> {
            ApprovalVO approvalVO = new ApprovalVO(approval);
            List<Long> uIds = approvalProcessList.parallelStream().filter(process -> approval.getId().equals(process.getApprovalId()))
                    .sorted(comparing(ApprovalProcess::getSeq)).map(ApprovalProcess::getUserId).collect(Collectors.toList());
            List<String> nicks = new ArrayList<>(uIds.size());
            uIds.forEach(uid -> nicks.add(userMap.get(uid)));
            String approver = StringUtils.join(nicks, "|");
            approvalVO.setApprover(approver);
            records.add(approvalVO);
        });

        result.setRecords(records);
        return result;
    }

    @Override
    public boolean delete(Long approvalId) throws Exception {
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
        flag = approvalService.delete(Condition.create().where("approval_id={0}", approvalId));
        if (!flag) {
            throw new BaseException("审批结果数据删除异常或已经被删除");
        }
        return true;
    }

    @Override
    public BaseExModel createApprovalExcel(Long orgId, Long userId, Long modelId, Integer state, String title, String createTimeStart, String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws Exception {
        List<ApprovalExcelVO> exportData = getExportData(orgId, userId, modelId, state, title, createTimeStart, createTimeEnd, finishTimeStart, finishTimeEnd);

        ApprovalExModel approvalExModel = new ApprovalExModel();
        // 从redis缓存中获取approvalExData
        ApprovalExData approvalExData = approvalRedisService.get(String.valueOf(orgId + userId));
        Map<String, List<ApprovalTemplVO>> temMap = approvalExData.getTemMap();

        List<ModelItem> modelItemList = modelItemMapper.selectAll(orgId);

        // 工作表名称处理
        List<Long> modelIdList = exportData.parallelStream().map(ApprovalExcelVO::getModelId).collect(Collectors.toSet()).stream().collect(Collectors.toList());
        if (null != modelId) {
            modelIdList.add(modelId);
        }
        List<ModelL> modelLS;
        if (CollectionUtils.isNotEmpty(modelIdList)) {
            modelLS = modelService.selectBatchIds(modelIdList);
        } else {
            modelLS = new ArrayList<>();
        }
        // 文件名
        String currentTime = DateUtil.dateFormmat(DateUtil.getCurrentTime(), "yyyyMMddHHmmss");
        StringBuilder fileName = new StringBuilder().append(currentTime).append(ApprovalExConsts.SEPARATOR_POINT).append(ApprovalExConsts.Type_xls);
        List<ExcelModel> excelModelList = new ArrayList<>();
        String TABLE_HEADER = "报表生成日期：" + DateUtil.dateFormmat(DateUtil.getCurrentTime(), DateUtil.DATE_FORMAT_1);
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
                    if (StringUtils.isNotBlank(createTimeStart) && StringUtils.isNotBlank(createTimeEnd)) {
                        statisticDate = statisticDate + createTimeStart + " —— " + createTimeEnd + "       ";
                    } else {
                        String orgCreateTime = DateUtil.dateFormmat(orgReadisService.get(String.valueOf(orgId)).getCreateTime(), DateUtil.DATE_FORMAT_2);
                        statisticDate = statisticDate + orgCreateTime + " —— " + DateUtil.dateFormmat(new Date(), DateUtil.DATE_FORMAT_2) + "       ";
                    }
                    excelModel.setTableHeader(statisticDate + TABLE_HEADER);
                    if (exportData.get(0).getModelVersion() == null) {
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
            if (StringUtils.isNotBlank(createTimeStart) && StringUtils.isNotBlank(createTimeEnd)) {
                statisticDate = statisticDate + createTimeStart + " —— " + createTimeEnd + "       ";
            } else {
                String orgCreateTime = DateUtil.dateFormmat(orgReadisService.get(String.valueOf(orgId)).getCreateTime(), DateUtil.DATE_FORMAT_2);
                statisticDate = statisticDate + orgCreateTime + " —— " + DateUtil.dateFormmat(new Date(), DateUtil.DATE_FORMAT_2) + "       ";
            }

            excelModel.setTableHeader(statisticDate + TABLE_HEADER);
            excelModelList.add(excelModel);
        }
        approvalExModel.setExcelModelList(excelModelList);
        approvalExModel.setFileName(fileName.toString());

        //  保存导出记录
        saveExportLog(orgId, userId, modelId, fileName.toString(), exportData);
        return approvalExModel;
    }

    /**
     * 获取需要导出的审批数据
     *
     * @param orgId           企业主键
     * @param modelId         模型主键, 审批类型, 可空(全部)
     * @param state           审批状态  0:审批中 1:审批完成 2:已撤回, 可空(全部)
     * @param title           审批标题
     * @param createTimeStart 发起时间_开始
     * @param createTimeEnd   发起时间_结束
     * @param finishTimeStart 完成时间_开始
     * @param finishTimeEnd   完成时间_结束
     * @return
     * @throws Exception
     */
    private List<ApprovalExcelVO> getExportData(Long orgId, Long userId, Long modelId, Integer state, String title, String createTimeStart,
                                                String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws Exception {
        approvalRedisService.remove(String.valueOf(orgId + userId));
        List<Approval> approvalList = this.getList(orgId, modelId, state, title, createTimeStart, createTimeEnd, finishTimeStart, finishTimeEnd);
        List<ApprovalExcelVO> excelVOList = new ArrayList<>();
        Map<String, List<ApprovalTemplVO>> tmpMap = new HashMap<>(1);
        // 获取审批人
        List<Long> approvalIds = approvalList.stream().map(Approval::getId).collect(Collectors.toList());
        List<ApprovalProcess> approvalProcessList = approvalProcessService.selectList(new EntityWrapper<ApprovalProcess>().in("approval_id", approvalIds));
        if (CollectionUtils.isEmpty(approvalProcessList)) {
            logger.error("审批流程信息不存在");
            throw new BaseException("审批流程信息不存在");
        }
        Set<Long> userIdSet = approvalProcessList.stream().map(ApprovalProcess::getUserId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIdSet)) {
            logger.error("审批人信息不存在");
            throw new BaseException("审批人信息不存在");
        }
        List<Long> userIds = new ArrayList<>(userIdSet.size());
        userIds.addAll(userIdSet);
        List<ApprovalUser> users = approvalUserService.selectBatchIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            logger.error("获取审批人信息不存在");
//            throw new BaseException("获取审批人信息不存在");
        }
        // 封装审批人主键与昵称 （key = id, value = nick）
        Map<Long, String> userMap = users.stream().collect(Collectors.toMap(ApprovalUser::getId, ApprovalUser::getName));
        // 审批状态
        String status = "";
        // 审批结果
        String result = "";
        String timeConsuming = "";

        List<Long> userIdList = approvalList.stream().map(Approval::getUserId).collect(Collectors.toList());
        List<DeptVO> deptNameList = new ArrayList<>();
        if (userIdList != null && !userIdList.isEmpty()) {
            String[] userIdArray = userIdList.toArray(new String[userIdList.size()]);
            // 调用企业服务
//            String jsonData = deptUserDao.getDeptNameListByUserIds(orgId, userIdArray);
//            Type type = new TypeReference<BaseResult<List<DeptVO>>>() {
//            }.getType();
//            BaseResult<List<DeptVO>> baseResult = JSONObject.parseObject(jsonData, type);
//            if (!BotongBaseCode.SUCCESS.equals(baseResult.getCode())) {
//                throw new BaseException("调用企业服务获取部门信息失败");
//            }
//            deptNameList = baseResult.getData();
        }

        // 获取该企业下所有modelItem
        List<ModelItem> modelItems = modelItemMapper.selectAll(orgId);
        // 获取该企业下所有的审批属性信息
        List<ApprovalAttr> approvalAttrList = null;
        // 从redis缓存中批量获取用户信息
        List<Long> uIdList = approvalList.parallelStream().map(Approval::getUserId).collect(Collectors.toList());
        List<UserVO> userVOS = userRedisService.getByUserIdList(null);
        // 查询编辑审批表单后填写的所有审批信息数据
        List<ApproveAttributeVO> approveAttrList = approvalAttrMapper.selectAttrListByOrgId(orgId);
        for (Approval approval : approvalList) {
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
                status = "";
                // 审批结果
                if (null != approval.getResult() && approval.getResult() == 1) {
                    result = "同意";
                } else if (null != approval.getResult() && approval.getResult() == 2) {
                    result = "拒绝";
                } else if (null != approval.getResult() && approval.getResult() == 4) {
                    result = "已撤销";
                }
                excelVO.setResult(result);
                result = "";
                // 审批发起时间
                excelVO.setCreateTime(null != approval.getCreateTime() ? approval.getCreateTime() : null);
                // 审批结束时间
                excelVO.setFinishTime(null != approval.getFinishTime() ? approval.getFinishTime() : null);
                List<UserVO> userVO1 = userVOS.parallelStream().filter(userVO -> userVO.getUserId().equals(approval.getUserId())).collect(Collectors.toList());
                if (!userVO1.isEmpty()) {
                    // 发起人姓名
                    excelVO.setUserName(userVO1.get(0).getUserNick());
                }
                // 审批人
                List<Long> uIds = approvalProcessList.parallelStream().filter(process -> approval.getId().equals(process.getApprovalId()))
                        .sorted(comparing(ApprovalProcess::getSeq)).map(ApprovalProcess::getUserId).collect(Collectors.toList());
                List<String> nicks = new ArrayList<>(uIds.size());
                uIds.forEach(uid -> nicks.add(userMap.get(uid)));
                String approver = StringUtils.join(nicks, "|");
                excelVO.setApprovalName(approver);
                // 发起人部门
                if (deptNameList != null && !deptNameList.isEmpty()) {
                    List<String> deptName = deptNameList.stream().filter(deptVO -> deptVO.getUserId().equals(approval.getUserId()))
                            .map(DeptVO::getDeptName).collect(Collectors.toList());
                    excelVO.setDeptName(deptName.get(0));
                } else {
                    excelVO.setDeptName("");
                }
                // 耗时
                if (null != approval.getFinishTime() && null != approval.getCreateTime()) {
                    long time = approval.getFinishTime() - approval.getCreateTime();
                    timeConsuming = DateUtil.getTime(time);
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
            ApprovalExData approvalExData = approvalRedisService.get(String.valueOf(orgId + userId));
            tmpMap.put(approval.getModelId() + "-" + approval.getModelVersion(), approvalExData.getApprovalTempVOList());
        }
        ApprovalExData exData = new ApprovalExData(tmpMap);
        approvalRedisService.put(String.valueOf(orgId + userId), exData, 30 * 60);
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
    private Map<String, Object> approvalDataByType(Long orgId, Long userId, Approval approval, List<ModelItem> modelItems, List<ApprovalAttr> attrList, List<ApproveAttributeVO> approveAttrs) {
        approvalRedisService.remove(String.valueOf(orgId + userId));
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
                } else if (type == 12) {
                    ApproveAttrVO attrVo = new ApproveAttrVO(attr);
                    map.put(attrVo.getLabel(), attrVo.getImages());
                    approvalTemplVO.setCKey(attrVo.getLabel());
                } else if (type == 11) {
                    ApproveAttrVO attrVo = new ApproveAttrVO(attr);
                    map.put(attrVo.getLabel(), attrVo.getFiles());
                    approvalTemplVO.setCKey(attrVo.getLabel());
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
        approvalRedisService.put(String.valueOf(orgId + userId), approvalExData, 30 * 60);
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
    private boolean saveExportLog(Long orgId, Long userId, Long modelId, String fileName, List<ApprovalExcelVO> exportData) throws BaseException {

        if (null == orgId) {
            throw new BaseException("该企业不存在");
        }
        if (null == userId) {
            throw new BaseException("该用户不存在");
        }
        String modelIds;
        Set<Long> set = new HashSet<>();
        ExportLog exportLog = new ExportLog();
        if (exportData.isEmpty()) {
            exportLog.setModelId("");
        }
        exportData.forEach(approvalExcelVO -> set.add(approvalExcelVO.getModelId()));
        if (StringUtils.isBlank(modelId.toString()) && set.size() > 1) {
            modelIds = "";
            for (Long m : set) {
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
        exportLog.setId(IDUtils.getID());
        exportLog.setCreateTime(DateUtil.getCurrentTime().getTime());
        exportLog.setOrg_id(orgId);
        exportLog.setUserId(userId);
        boolean insert = exportLogService.insert(exportLog);

        if (!insert) {
            throw new BaseException("保存导出记录失败");
        }
        return true;
    }

    private Wrapper<Approval> getWrapper(Long oid, Long mid, Integer state, String title, String createTimeStart, String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws BaseException {
        Wrapper<Approval> wrapper = new EntityWrapper<>();
        if (null == oid) {
            throw new BaseException("主键不存在");
        }

        wrapper.eq("org_id", oid);

        if (null != mid) {
            wrapper.in("model_id", mid.toString());
        }

        if (state != null) {
            wrapper.eq("state", state);
        }

        if (StringUtils.isNotBlank(title)) {
            wrapper.like("title", title);
        }

        if (StringUtils.isNotBlank(createTimeStart)) {
            wrapper.ge("DATE_FORMAT(create_time,'%Y-%m-%d')", createTimeStart);
        }

        if (StringUtils.isNotBlank(createTimeEnd)) {
            wrapper.le("DATE_FORMAT(create_time,'%Y-%m-%d')", createTimeEnd);
        }

        if (StringUtils.isNotBlank(finishTimeStart)) {
            wrapper.ge("DATE_FORMAT(finish_time,'%Y-%m-%d')", finishTimeStart);
        }

        if (StringUtils.isNotBlank(finishTimeEnd)) {
            wrapper.le("DATE_FORMAT(finish_time,'%Y-%m-%d')", finishTimeEnd);
        }
        wrapper.orderBy("create_time", false);
        return wrapper;
    }

    private List<Approval> getList(Long oid, Long mid, Integer state, String title, String createTimeStart, String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws BaseException {
        return this.selectList(getWrapper(oid, mid, state, title, createTimeStart, createTimeEnd, finishTimeStart, finishTimeEnd).orderBy("model_id", true));
    }

    private Page<Approval> getPage(Page<Approval> page, Long oid, Long mid, Integer state, String title, String createTimeStart, String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws BaseException {
        return this.selectPage(page, getWrapper(oid, mid, state, title, createTimeStart, createTimeEnd, finishTimeStart, finishTimeEnd));
    }

    /**
     * 保存审批人信息
     *
     * @param sendUserIds 审批人主键
     * @param approval    审批对象
     * @return
     */
    private boolean saveApprovalUser(String sendUserIds, Approval approval) throws BaseException {
        boolean flag = false;
        if (sendUserIds != null) {
            // 去除可能的重复数据
            Set<Long> strSet = new HashSet<>();
            String[] userIds = sendUserIds.split(",");
            for (String str : userIds) {
                strSet.add(Long.valueOf(str));
            }
            Long[] ids = new Long[strSet.size()];
            int n = 0;
            for (Long string : strSet) {
                ids[n] = string;
                n++;
            }
            List<ApprovalUser> users = approvalUserService.selectList(Condition.create().in("id", ids));
            Set<ApprovalProcess> approvalProcesses = new HashSet<>();
            for (int j = 0; j < userIds.length; j++) {
                for (ApprovalUser user : users) {
                    if (Long.valueOf(userIds[j]).equals(user.getId())) {
                        ApprovalProcess approvalProcess = new ApprovalProcess();
                        approvalProcess.setId(IDUtils.getID());
                        approvalProcess.setApprovalId(approval.getId());
                        approvalProcess.setProcessTime(DateUtil.getCurrentTime().getTime());
                        approvalProcess.setProcessState(0);
                        approvalProcess.setSeq(j + 1);
                        approvalProcess.setUserId(user.getId());
                        approvalProcesses.add(approvalProcess);
                    }
                }
            }
            flag = approvalProcessService.insertBatch(new ArrayList<>(approvalProcesses));
            if (!flag) {
                throw new InsertMessageFailureException("批量保存审批人失败");
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
    private boolean saveCopyUser(String sendCopyIds, Approval approval) throws BaseException {
        boolean flag = false;
        if (sendCopyIds != null) {
            // 去除可能的重复数据
            Set<Long> strSet = new HashSet<>();
            String[] userIds = sendCopyIds.split(",");
            for (String str : userIds) {
                strSet.add(Long.valueOf(str));
            }
            Long[] ids = new Long[strSet.size()];
            int n = 0;
            for (Long string : strSet) {
                ids[n] = string;
                n++;
            }
            List<ApprovalUser> users = approvalUserService.selectList(Condition.create().in("id", ids));
            Set<CopyS> copyS = new HashSet<>();
            for (ApprovalUser user : users) {
                CopyS copyS1 = new CopyS();
                copyS1.setApprovalId(approval.getId());
                copyS1.setUserId(user.getId());
                copyS1.setId(IDUtils.getID());
                copyS1.setCopySType(0);
                copyS1.setCreateTime(DateUtil.getCurrentTime().getTime());
                copyS.add(copyS1);
            }
            flag = copySService.insertBatch(new ArrayList<>(copyS));
            if (!flag) {
                throw new InsertMessageFailureException("批量保存抄送人失败");
            }
        }
        return flag;
    }


}
