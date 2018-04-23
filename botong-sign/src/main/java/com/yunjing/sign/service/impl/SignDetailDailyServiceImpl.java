package com.yunjing.sign.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.Enum.DateStyle;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.model.SignConfigDaily;
import com.yunjing.sign.beans.model.SignDetailDaily;
import com.yunjing.sign.beans.model.SignDetailImgDaily;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.SignMapperParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.*;
import com.yunjing.sign.constant.SignConstant;
import com.yunjing.sign.dao.SignBaseMapper;
import com.yunjing.sign.dao.mapper.SignDetailDailyMapper;
import com.yunjing.sign.excel.BaseExModel;
import com.yunjing.sign.excel.ExcelModel;
import com.yunjing.sign.excel.SignExConsts;
import com.yunjing.sign.excel.SignExModel;
import com.yunjing.sign.processor.feign.UserRemoteService;
import com.yunjing.sign.processor.okhttp.UserRemoteApiService;
import com.yunjing.sign.service.ISignDetailDailyService;
import com.yunjing.sign.service.ISignDetailImgDailyService;
import com.yunjing.sign.service.ISignDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 签到明细 服务实现类
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@Service
public class SignDetailDailyServiceImpl extends ServiceImpl<SignDetailDailyMapper, SignDetailDaily> implements ISignDetailDailyService {

    @Autowired
    private ISignDetailImgDailyService iSignDetailImgDailyService;

    @Autowired
    private ISignDetailService iSignDetailService;

    @Autowired
    private SignDetailDailyMapper signDetailDailyMapper;

    /**
     * 签到
     *
     * @param signDetailParam 签到对象
     * @return 成功与否
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignDetailVO toSign(SignDetailParam signDetailParam) {
        SignDetailVO result = new SignDetailVO();
        SignConfigDaily signConfigModel = new SignConfigDaily().selectOne(new EntityWrapper<SignConfigDaily>().eq("org_id", signDetailParam.getOrgId()).eq("is_delete", 0));
        if (signConfigModel != null) {
            if (signConfigModel.getTimeStatus() == 1) {
                Date start = DateUtil.StringToDate(DateUtil.getDate(new Date()) + SignConstant.SEPARATE_STR_SPACE + signConfigModel.getStartTime(), DateStyle.YYYY_MM_DD_HH_MM);
                Date end = DateUtil.StringToDate(DateUtil.getDate(new Date())  + SignConstant.SEPARATE_STR_SPACE + signConfigModel.getEndTime(), DateStyle.YYYY_MM_DD_HH_MM);
                /**
                 * 1、如果开始时间为空，只比较结束时间是否到期。没到期，就不能打卡
                 * 2、开始时间不为空，结束时间不为空，比较开始时间和结束时间是否在范围
                 * 3、开始时间部位空，结束时间为空，只判断开始时间
                 */
                if (start == null) {
                    if (end != null && DateUtil.compareDate(new Date(), end) < 0) {
                        throw new UpdateMessageFailureException(600, "未到打卡时间");
                    }
                } else {
                    boolean compareFirst = DateUtil.compareDate(new Date(), start) > 0 && (end != null && DateUtil.compareDate(new Date(), end) < 0);
                    if (compareFirst) {
                        throw new UpdateMessageFailureException(600, "打卡未在时间范围");
                    }
                    if (DateUtil.compareDate(new Date(), start) > 0 && end == null) {
                        throw new UpdateMessageFailureException(600, "打卡时间超出范围");
                    }
                }
            }
        }
        SignDetailDaily signDetail = BeanUtils.map(signDetailParam, SignDetailDaily.class);
        result.setUserId(signDetail.getUserId());
        signDetail.setId(IDUtils.uuid());
        signDetail.insert();
        result.setSignDate(signDetail.getCreateTime());
        if (StringUtils.isNotBlank(signDetailParam.getImgUrls())) {
            SignDetailImgDaily detailImg;
            int i = 1;
            List<SignDetailImgDaily> list = new ArrayList<>();
            //组装图片对象
            for (String imgUrl : signDetailParam.getImgUrls().split(SignConstant.SEPARATE_STR)) {
                detailImg = new SignDetailImgDaily();
                detailImg.setSignDetailId(signDetail.getId());
                detailImg.setId(IDUtils.uuid());
                detailImg.setSort(i);
                detailImg.setUrl(imgUrl);
                list.add(detailImg);
                i++;
            }
            iSignDetailImgDailyService.insertBatch(list);
        }
        return result;
    }

    /**
     * 查询当日签到次数
     *
     * @param userId 用户id
     * @param orgId  组织id
     * @return       次数
     */
    @Override
    public int getSignCount(String userId, String orgId) {
        Date nowStart = DateUtil.stringToDate(DateUtil.converTime(new Date()));
        Date nowEnd = DateUtil.addDay(nowStart, 1);
        int count = new SignDetailDaily().selectCount(new EntityWrapper<SignDetailDaily>().eq("org_id", orgId).eq("user_id", userId).lt("create_time", nowEnd.getTime()).ge("create_time", nowStart.getTime()));
        return count;
    }

    /**
     * 签到统计接口
     *
     * @param userAndDeptParam 部门id和用户id组合
     * @return                  对象
     */
    @Override
    public SignListVO getCountInfo(UserAndDeptParam userAndDeptParam) {
        return iSignDetailService.getCountInfo(userAndDeptParam, signDetailDailyMapper);
    }

    /**
     * 按月查询我签到的明细
     *
     * @param signDetailParam 签到明细
     * @return                对象
     */
    @Override
    public MySignVO queryMonthInfo(SignDetailParam signDetailParam) {
        return iSignDetailService.queryMonthInfo(signDetailParam, signDetailDailyMapper);
    }

    /**
     * 获取导出模板
     *
     * @param userAndDeptParam
     * @return
     */
    @Override
    public BaseExModel createTempExcel(UserAndDeptParam userAndDeptParam) {
        Date startD = DateUtil.StringToDate(userAndDeptParam.getSignDate() + "-01", DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.getLastDayOfMonth(startD);
        List<SignExcelVO> exportData = iSignDetailService.getSignInList(userAndDeptParam, signDetailDailyMapper);
        if (exportData == null) {
            exportData = new ArrayList<>();
        }
        SignExModel signExModel = new SignExModel();

        List<ExcelModel> excelModelList = new ArrayList<>();
        ExcelModel excelModel = new ExcelModel();
        // 注入工作表名称
        excelModel.setSheetName("日常签到数据报表");

        excelModel.setNotice("日常"+ SignExConsts.NOTICE);
        // 注入文件名
        StringBuffer fileName = new StringBuffer().append("日常").append(SignExConsts.SHEET_NAME).append(SignExConsts.SEPARATOR_POINT).append(SignExConsts.Type_xls);
        excelModel.setFileName(fileName.toString());

        // 注入表头
        String statisticDate = "统计日期：";
        statisticDate = statisticDate + DateUtil.getDate(startD) + " —— " + DateUtil.getDate(endDate) + "       ";
        String tableHead = "报表生成日期："+ DateUtil.getDate(new Date());
        excelModel.setTableHeader(statisticDate + tableHead);

        // 注入数据项名称
        List<SignTemplVO> signTemplVOList = new ArrayList<>();
        for (int i = 1; i < SignConstant.BOTONG_SEVEN_VALUE; i++) {
            SignTemplVO signTemplVO = new SignTemplVO();
            signTemplVO.setCKey("图" + i);
            signTemplVOList.add(signTemplVO);
        }
        excelModel.setTitles(signTemplVOList);

        // 注入审批数据
        excelModel.setSignList(exportData);
        excelModelList.add(excelModel);
        signExModel.setExcelModelList(excelModelList);
        signExModel.setFileName(fileName.toString());
        return signExModel;
    }

    /**
     * 按月统计指定部门和人员的考勤信息
     *
     * @param userAndDeptParam 部门和人员
     * @return                  分页对象
     */
    @Override
    public PageWrapper<UserMonthListVO> staticsMonthInfo(UserAndDeptParam userAndDeptParam) {
        return iSignDetailService.staticsMonthInfo(userAndDeptParam, signDetailDailyMapper);
    }

    /**
     * 获取所有的签到明细
     *
     * @param signDetailParam       签到对象
     * @return                      签到列表
     */
    @Override
    public List<SignDetailDaily> queryDetailList(SignDetailParam signDetailParam) {
        Date nowStart = DateUtil.stringToDate(signDetailParam.getSignDate());
        Date nowEnd = DateUtil.addDay(nowStart, 1);
        List<SignDetailDaily> list = new SignDetailDaily().selectList(new EntityWrapper<SignDetailDaily>().eq("user_id", signDetailParam.getUserId()).lt("create_time", nowEnd.getTime()).ge("create_time", nowStart.getTime()).orderBy("create_time", false));
        return list;
    }
}
