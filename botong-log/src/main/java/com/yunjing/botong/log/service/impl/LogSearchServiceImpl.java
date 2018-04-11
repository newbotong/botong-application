package com.yunjing.botong.log.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.common.mongo.dao.Page;
import com.common.mongo.util.BeanUtils;
import com.common.mongo.util.PageWrapper;
import com.netflix.discovery.converters.Auto;
import com.yunjing.botong.log.constant.LogConstant;
import com.yunjing.botong.log.dao.LogDetailDao;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.excel.BaseExModel;
import com.yunjing.botong.log.excel.ExcelModel;
import com.yunjing.botong.log.excel.LogExConsts;
import com.yunjing.botong.log.excel.LogExModel;
import com.yunjing.botong.log.mapper.LogTemplateFieldMapper;
import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.params.SearchParam;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.ILogSearchService;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.botong.log.vo.*;
import com.yunjing.mommon.Enum.DateStyle;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日志搜索服务实现类
 *
 * @author jingwj
 * @date 2018/4/2 10:55
 */
@Slf4j
@Service
public class LogSearchServiceImpl implements ILogSearchService {

    @Autowired
    private LogDetailDao logDetailDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AppCenterService appCenterService;

    @Autowired
    LogTemplateService logTemplateService;

    private Type memberType = new TypeReference<List<Member>>() {
    }.getType();



    /**
     * 我收到的日志列表
     *
     * @param receviedParam         收到的参数对象
     * @return                      分页日志列表
     */
    @Override
    public PageWrapper<LogDetailVO> receivePage(ReceviedParam receviedParam) {
        PageWrapper<LogDetail> detailResult = logDetailDao.find(receviedParam.getPageNo(), receviedParam.getPageSize(), receviedParam.getOrgId(), receviedParam.getUserId(), receviedParam.getReadStatus(), receviedParam.getSendUserIds(), LogConstant.BOTONG_ONE_STR);
        PageWrapper<LogDetailVO> result = convertResults(receviedParam, detailResult);
        return result;
    }

    /**
     * 查询我发送的日志列表
     *
     * @param receviedParam 日志参数对象
     * @return 日志明细列表
     */
    @Override
    public PageWrapper<LogDetailVO> sendPage(ReceviedParam receviedParam) {
        PageWrapper<LogDetail> detailResult = logDetailDao.find(receviedParam.getPageNo(), receviedParam.getPageSize(), receviedParam.getOrgId(), null, null, null, LogConstant.BOTONG_ZERO_STR);
        PageWrapper<LogDetailVO> result = convertResults(receviedParam, detailResult);
        return result;
    }

    /**
     * 查询我发送的日志列表
     *
     * @param logId 日志Id
     * @return 日志明细列表
     */
    @Override
    public boolean read(String logId, String userId) {
        logDetailDao.updateReadByLogId(logId, userId);
        return true;
    }

    /**
     * 未读设置为已读
     *
     * @param receviedParam 日志参数对象
     * @return 成功与否
     */
    @Override
    public boolean read(ReceviedParam receviedParam) {
        logDetailDao.updateReadAll(receviedParam.getOrgId(), receviedParam.getUserId(), receviedParam.getSendUserIds());
        return true;
    }

    /**
     * 删除日志
     *
     * @param logId  日志id
     * @param userId 用户id
     * @return 成功与否
     */
    @Override
    public boolean delete(String logId, String userId) {
        if (StringUtils.isNotBlank(logId)) {
            logDetailDao.delete(logId, userId);
        }
        return true;
    }

    /**
     * 根据条件查询日志列表
     *
     * @param searchParam 日志参数对象
     * @return 日志明细列表
     */
    @Override
    public PageWrapper<LogDetailVO> findPage(SearchParam searchParam) {
        //当没有选择范围则查询该企业下所有的员工
        if (searchParam.getDeptIds().length == 0 && searchParam.getUserIds().length == 0) {
            searchParam.getDeptIds()[0] = searchParam.getOrgId();
        }
        List<Member> memList = appCenterService.findSubLists(searchParam.getDeptIds(), searchParam.getUserIds());
        List<String> memIds = new ArrayList<>();
        Map<String, Member> memberMap = new HashMap<>(16);
        for(Member member : memList) {
            memIds.add(member.getId());
            memberMap.put(member.getId(), member);
        }

        PageWrapper<LogDetail> detailResult = logDetailDao.findByConditionPage(searchParam.getPageNo(), searchParam.getPageSize(), searchParam.getOrgId(), memIds, searchParam.getSubmitType(), searchParam.getStartDate(), searchParam.getEndDate());
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (detailResult.getRecords() != null && detailResult.getSize() > 0) {
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            List<MemberVO> userList;
            for (LogDetail detail : detailResult.getRecords()) {
                vo = BeanUtils.map(detail, LogDetailVO.class);
                vo.setUser(memberMap.get(detail.getMemberId()));
                resultRecord.add(vo);
            }
            result.setCurrent(detailResult.getCurrent());
            result.setPages(detailResult.getPages());
            result.setSize(detailResult.getSize());
            result.setTotal(detailResult.getTotal());
            result.setRecords(resultRecord);
        }
        return result;
    }

    /**
     * 删除日志
     *
     * @param logIds 日志ids
     * @return 成功与否
     */
    @Override
    public boolean batchDelete(String[] logIds) {
        if (logIds != null && logIds.length > 0) {
            logDetailDao.deleteByIds(logIds);
        }
        return true;
    }

    /**
     * mongo中拿到的实体转为返回的vo列表
     * @param receviedParam         参数对象
     * @param detailResult          明细结果
     * @return                      带分页参数的明细列表
     */
    private PageWrapper<LogDetailVO> convertResults(ReceviedParam receviedParam, PageWrapper<LogDetail> detailResult){
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (detailResult.getRecords() != null && detailResult.getSize() > 0) {
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            Member userVO;
            Set<Object> userIds = new HashSet<>();
            for (LogDetail detail : detailResult.getRecords()) {
                //设置读取状态
                if(detail.getReadUserId().contains(String.valueOf(receviedParam.getUserId()))) {
                    detail.setState(LogConstant.BOTONG_ONE_STR);
                } else {
                    detail.setState(LogConstant.BOTONG_ZERO_STR);
                }
                //放入发送人，已读，未读人员集合
                userIds.add(detail.getMemberId().toString());
                userIds.addAll(detail.getReadUserId());
                userIds.addAll(detail.getSendToUserId());
                userIds.addAll(detail.getUnreadUserId());
            }

            List listT = redisTemplate.opsForHash().multiGet(LogConstant.BOTONG_ORG_MEMBER, userIds);
            Member memberVO;
            List<Member> list = new ArrayList<>();
            if (listT != null && !listT.isEmpty()) {
                list = JSON.parseObject(listT.toString(), memberType);
            }

            Map<String, Member> map = new HashMap<>(16);
            for (Member objVO : list) {
                if (objVO == null) {
                    continue;
                }
                map.put(objVO.getId(), objVO);
            }
            List<Member> userList;
            for (LogDetail detail : detailResult.getRecords()) {
                vo = BeanUtils.map(detail, LogDetailVO.class);
                userList = new ArrayList<>();
                for (String userId : detail.getReadUserId()) {
                    userVO = map.get(userId);
                    if (userVO != null) {
                        userList.add(userVO);
                    }
                }
                vo.setUser(map.get(detail.getMemberId().toString()));
                vo.setReadUsers(userList);
                resultRecord.add(vo);
            }
            result.setCurrent(detailResult.getCurrent());
            result.setPages(detailResult.getPages());
            result.setSize(detailResult.getSize());
            result.setTotal(detailResult.getTotal());
            result.setRecords(resultRecord);
        }
        return result;
    }


    /**
     * 查询所有的日志列表
     *
     * @param searchParam
     * @return
     */
    @Override
    public List<LogExcelVO> findAll(SearchParam searchParam) {
        //当没有选择范围则查询该企业下所有的员工
        if (searchParam.getDeptIds() != null && searchParam.getDeptIds().length == 0 && searchParam.getUserIds() != null && searchParam.getUserIds().length == 0) {
            String[] deptIds = {searchParam.getOrgId()};
            searchParam.setDeptIds(deptIds);
        }
        List<Member> memList = appCenterService.findSubLists(searchParam.getDeptIds(), searchParam.getUserIds());
        List<String> memIds = new ArrayList<>();
        Map<String, Member> memberMap = new HashMap<>();
        for(Member member : memList) {
            memIds.add(member.getId());
            memberMap.put(member.getId(), member);
        }
        List<LogExcelVO> resultRecord = new ArrayList<>();
        List<LogDetail> detailResult = logDetailDao.findByConditionAll(searchParam.getOrgId(), memIds, searchParam.getSubmitType(), searchParam.getStartDate(), searchParam.getEndDate());
        if (detailResult != null && !detailResult.isEmpty()) {
            LogExcelVO logExcelVO;
            List<AttrValueVO> logData;
            AttrValueVO attrValueVO;
            for (LogDetail detail : detailResult) {
                logExcelVO = new LogExcelVO();
                logExcelVO.setSender(memberMap.get(detail.getMemberId()).getMemberName());
                logExcelVO.setSendTime(DateUtil.DateToString(detail.getSubmitTime(), DateStyle.YYYY_MM_DD_HH_MM_SS));
                logExcelVO.setDeptName(StringUtils.join(memberMap.get(detail.getMemberId()).getDeptNames(), LogConstant.SEPARATE_STR));
                logExcelVO.setLogId(detail.getLogId());
                logExcelVO.setType(detail.getSubmitType().toString());
                logData = new ArrayList<>();
                attrValueVO = new AttrValueVO();
                for (LogConentVO conentVO : detail.getContents()) {
                    attrValueVO.setEkey(conentVO.getKey());
                    attrValueVO.setCkey(conentVO.getName());
                    attrValueVO.setAttrVal(conentVO.getValue());
                    logData.add(attrValueVO);
                }
                AttrValueVO attrValueVO1 = new AttrValueVO();
                attrValueVO1.setCkey("图片地址");
                String logImgs = StringUtils.join(detail.getLogImages(), " \r\n");
                attrValueVO1.setAttrVal(logImgs);
                logData.add(attrValueVO1);
                logExcelVO.setListValue(logData);
                resultRecord.add(logExcelVO);
            }
        }
        return resultRecord;
    }

    /**
     * 根据条件执行导出日志信息
     *
     * @param searchParam 参数对象
     * @return excel包装类
     * @throws Exception
     */
    @Override
    public BaseExModel createLogExcel(SearchParam searchParam) throws Exception {
        Date time = new Date();
        List<LogExcelVO> logExcelVOList = findAll(searchParam);
        List<ExcelModel> excelModelList = new ArrayList<>();
        LogExModel logExModel = new LogExModel();
        Date time2 = new Date();
        log.info("从mongo查询到处理完毕总耗时：" + DateUtil.calculateIntervalSecond(time, time2));
        Map<String, List<LogTemplateFieldVo>> model = logTemplateService.queryFields(searchParam);
        StringBuilder fileName = new StringBuilder().append(LogExConsts.NOTICE).append(LogExConsts.SEPARATOR_POINT).append(LogExConsts.TYPE_XLSX);
        String TABLE_HEADER = "报表生成日期："+ DateUtil.getDateTime(new Date());
        for (String key : model.keySet()) {
            if (searchParam.getSubmitType() != 0) {
                if (model.get(key).get(0).getType() != searchParam.getSubmitType().intValue()) {
                    continue;
                }
            }

            ExcelModel excelModel = new ExcelModel();
            // 注入工作表名称
            excelModel.setSheetName(key);

            // 注入文件名
            excelModel.setFileName(fileName.toString());

            // 注入表头
            String statisticDate = "统计日期：";
            if (StringUtils.isNotBlank(searchParam.getStartDate()) && StringUtils.isNotBlank(searchParam.getEndDate())) {
                statisticDate = statisticDate + searchParam.getStartDate() + " —— " + searchParam.getEndDate() + "       ";
            }
            excelModel.setTableHeader(statisticDate + TABLE_HEADER);

            // 注入数据项名称
            List<LogTemplVO> logTemplVOList = new ArrayList<>();
            for (LogTemplateFieldVo modelItem : model.get(key)) {
                LogTemplVO logTemplVO = new LogTemplVO();
                logTemplVO.setCKey(modelItem.getFieldLabel());
                logTemplVO.setVal(modelItem.getFieldLabel());
                logTemplVO.setEKey(modelItem.getFieldName());
                logTemplVOList.add(logTemplVO);
            }
            LogTemplVO logTemplVO2 = new LogTemplVO();
            logTemplVO2.setCKey("图片地址");
            logTemplVO2.setEKey(LogExConsts.CELL_NAME_IMG_EN);
            logTemplVOList.add(logTemplVO2);
            excelModel.setTitles(logTemplVOList);

            // 注入日志数据
            List<LogExcelVO> collect = logExcelVOList.stream().filter(logExcelVO -> logExcelVO.getType().equals(model.get(key).get(0).getType().toString()))
                    .collect(Collectors.toList());
            excelModel.setLogList(collect);
            excelModelList.add(excelModel);
        }
        logExModel.setExcelModelList(excelModelList);
        logExModel.setFileName(fileName.toString());
        Date time3 = new Date();
        log.info("数据注入excel表耗时：" + DateUtil.calculateIntervalSecond(time, time2));
        return logExModel;
    }
}
