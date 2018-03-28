package com.yunjing.sign.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.Enum.DateStyle;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.model.SignConfigModel;
import com.yunjing.sign.beans.model.SignDetail;
import com.yunjing.sign.beans.model.SignDetailImg;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.SignMapperParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.*;
import com.yunjing.sign.constant.SignConstant;
import com.yunjing.sign.dao.SignBaseMapper;
import com.yunjing.sign.dao.mapper.SignDetailMapper;
import com.yunjing.sign.excel.BaseExModel;
import com.yunjing.sign.excel.ExcelModel;
import com.yunjing.sign.excel.SignExConsts;
import com.yunjing.sign.excel.SignExModel;
import com.yunjing.sign.processor.feign.UserRemoteService;
import com.yunjing.sign.service.ISignDetailImgService;
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
public class SignDetailServiceImpl extends ServiceImpl<SignDetailMapper, SignDetail> implements ISignDetailService {

    @Autowired
    private ISignDetailImgService iSignDetailImgService;

    @Autowired
    private SignDetailMapper signDetailMapper;

    @Autowired
    private UserRemoteService userRemoteService;

    /**
     * 签到
     *
     * @param signDetailParam 签到对象
     * @return 成功与否
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toSign(SignDetailParam signDetailParam) {
        SignDetail signDetail = BeanUtils.map(signDetailParam, SignDetail.class);
        SignConfigModel signConfigModel = new SignConfigModel().selectOne(new EntityWrapper<SignConfigModel>().eq("org_id", signDetailParam.getOrgId()).eq("is_delete", 0));
        if (signConfigModel != null) {
            if (signConfigModel.getTimeStatus() == 1) {
                Date nowStart = DateUtil.stringToDate(DateUtil.converTime(new Date()));
                Date nowEnd = DateUtil.addDay(nowStart, 1);
                int count = new SignDetail().selectCount(new EntityWrapper<SignDetail>().eq("org_id", signDetailParam.getOrgId()).eq("user_id", signDetailParam.getUserId()).lt("create_time", nowEnd.getTime()).ge("create_time", nowStart.getTime()));
                Date nowStart1 = DateUtil.StringToDate(DateUtil.converTime(new Date()) + " " + signConfigModel.getEndTime(), DateStyle.YYYY_MM_DD_HH_MM);
                if (count >= 1) {
                    if(nowStart1.getTime() >  System.currentTimeMillis()) {
                        throw new UpdateMessageFailureException(600, "时间未到，不能打卡");
                    }
                }
            }
        }

        boolean result = signDetail.insert();
        if (StringUtils.isNotBlank(signDetailParam.getImgUrls())) {
            SignDetailImg detailImg;
            int i = 1;
            List<SignDetailImg> list = new ArrayList<>();
            for (String imgUrl : signDetailParam.getImgUrls().split(SignConstant.SEPARATE_STR)) {
                detailImg = new SignDetailImg();
                detailImg.setSignDetailId(signDetail.getId());
                detailImg.setSort(i);
                detailImg.setUrl(imgUrl);
                list.add(detailImg);
                i++;
            }
            iSignDetailImgService.insertBatch(list);
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
        int count = new SignDetail().selectCount(new EntityWrapper<SignDetail>().eq("org_id", orgId).eq("user_id", userId).lt("create_time", nowEnd.getTime()).ge("create_time", nowStart.getTime()));
        return count;
    }

    /**
     * 签到统计接口
     *
     * @param userAndDeptParam 部门id和用户id组合
     * @return
     */
    @Override
    public SignListVO getCountInfo(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper) {
        String[] deptIds = StringUtils.split(userAndDeptParam.getDeptIds(),",");
        String[] userIdCs = StringUtils.split(userAndDeptParam.getUserIds(),",");
        ResponseEntityWrapper<List<SignUserInfoVO>> memResult = userRemoteService.findSubLists(deptIds, userIdCs);

        List<SignUserInfoVO> userList =  memResult.getData();
        if(userList.size() == 0) {
            return null;
        }
        Map<Long, SignUserInfoVO> map = new HashMap<>();
        List<Long>  ids = new ArrayList<>();
        for(SignUserInfoVO obj : userList) {
            map.put(obj.getId(), obj);
            ids.add(obj.getId());
        }
        String userIds = StringUtils.join(ids, ",");
        long startDate = DateUtil.stringToDate(userAndDeptParam.getSignDate()).getTime();
        long endDate = DateUtil.addDay(DateUtil.stringToDate(userAndDeptParam.getSignDate()), 1).getTime();
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserIds(userIds);
        signMapperParam.setStartDate(startDate);
        signMapperParam.setEndDate(endDate);
        List<SignUserInfoVO> userIdList = mapper.getCountInfo(signMapperParam);
        List<SignUserInfoVO> signList = new ArrayList<>();
        List<SignUserInfoVO> unSignList = new ArrayList<>();

        for(SignUserInfoVO vo : userIdList) {
            map.get(vo.getId()).setSignState(1);
            signList.add(map.get(vo.getId()));
        }
        for (Long key : map.keySet()) {
            if (map.get(key).getSignState() != 1) {
                unSignList.add(map.get(key));
            }
        }
        SignListVO result = new SignListVO();
        result.setSignList(signList);
        result.setUnSignList(unSignList);
        return result;
    }

    /**
     * 按月查询我签到的明细
     *
     * @param signDetailParam 签到明细
     * @return
     */
    @Override
    public MySignVO queryMonthInfo(SignDetailParam signDetailParam, SignBaseMapper mapper) {
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserId(signDetailParam.getUserId());
        Date startD = DateUtil.StringToDate(signDetailParam.getSignDate(), DateStyle.YYYY_MM);
        Long startDate = startD.getTime();
        long endDate = DateUtil.getLastDayOfMonth(startD).getTime();
        signMapperParam.setStartDate(startDate);
        signMapperParam.setEndDate(endDate);
        List<SignDetailVO> signList = mapper.queryMonthInfo(signMapperParam);
        MySignVO result = new MySignVO();
        result.setUserId(signDetailParam.getUserId());
        result.setSignCount(signList.size());

        Map<String, List<SignDetailVO>> map = new LinkedHashMap<>();
        String dateKey;
        List<SignDetailVO> signDetailVOS;
        for (SignDetailVO vo : signList) {
            dateKey = DateUtil.DateToString(DateUtil.convertLongToDate(vo.getSignDate()),DateStyle.YYYY_MM_DD);
            if (map.get(dateKey) != null) {
                map.get(dateKey).add(vo);
            } else {
                signDetailVOS = new ArrayList<SignDetailVO>();
                signDetailVOS.add(vo);
                map.put(dateKey, signDetailVOS);
            }
        }
        SignDateVO dateVO;
        List<SignDateVO> dateList = new ArrayList<>();
        for (String key : map.keySet()) {
            dateVO = new SignDateVO();
            dateVO.setSignDate(key);
            dateVO.setDetailList(map.get(key));
            dateList.add(dateVO);
        }
        result.setSignList(dateList);
        return result;
    }

    /**
     * 按月统计指定部门和人员的考勤信息
     *
     * @param userAndDeptParam 部门和人员
     * @return
     */
    @Override
    public PageWrapper<UserMonthListVO> staticsMonthInfo(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper) {
        String[] deptIds = StringUtils.split(userAndDeptParam.getDeptIds(),",");
        String[] userIdCs = StringUtils.split(userAndDeptParam.getUserIds(),",");
        ResponseEntityWrapper<PageWrapper<SignUserInfoVO>> memResult = userRemoteService.findMemberPage(deptIds, userIdCs, userAndDeptParam.getPageNo(), userAndDeptParam.getPageSize());
        PageWrapper<SignUserInfoVO> page = memResult.getData();
        List<SignUserInfoVO> userList = page.getRecords();
        if(userList.size() == 0) {
            return null;
        }
        Map<Long, UserMonthVO> map = new HashMap<>();
        List<Long>  ids = new ArrayList<>();
        Date startD = DateUtil.StringToDate(userAndDeptParam.getSignDate() + "-01", DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.getLastDayOfMonth(startD);
        int dayB = DateUtil.getIntervalDays(startD, endDate);
        UserMonthVO userMonthVO;
        SignMonthVO vo;
        Map<String, SignMonthVO> monthList;
        for(SignUserInfoVO obj : userList) {
            userMonthVO = new UserMonthVO();
            userMonthVO.setDeptName(StringUtils.join(obj.getDeptNames(), SignConstant.SEPARATE_STR));
            userMonthVO.setPostName(obj.getPosition());
            userMonthVO.setUserName(obj.getName());
            monthList = new LinkedHashMap<String, SignMonthVO>();
            for (int j = 0; j <= dayB; j++) {
                vo = new SignMonthVO();
                Date dDate = DateUtil.addDay(startD, j);
                vo.setSignWeek(DateUtil.getWeek(dDate).getNumber());
                vo.setSignTime(dDate.getTime());
                monthList.put(DateUtil.getDate(dDate), vo);
            }
            userMonthVO.setMonthList(monthList);
            map.put(obj.getId(), userMonthVO);
            ids.add(obj.getId());
        }
        String userIds = StringUtils.join(ids, ",");
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserIds(userIds);
        signMapperParam.setStartDate(startD.getTime());
        signMapperParam.setEndDate(endDate.getTime());
        List<SignMonthVO> userIdList = mapper.staticsMonthInfo(signMapperParam);
        List<SignUserInfoVO> signList = new ArrayList<>();
        List<SignUserInfoVO> unSignList = new ArrayList<>();
        for(SignMonthVO vo1 : userIdList) {
            map.get(vo1.getUserId()).getMonthList().get(vo1.getSignDate()).setSignCount(vo1.getSignCount());
        }
        List<UserMonthListVO> list = new ArrayList<UserMonthListVO>();
        UserMonthListVO userMonthListVO;
        for (long key : map.keySet()) {
            userMonthListVO = new UserMonthListVO();
            userMonthListVO.setDeptName(map.get(key).getDeptName());
            userMonthListVO.setPostName(map.get(key).getPostName());
            userMonthListVO.setUserName(map.get(key).getUserName());
            userMonthListVO.setMonthList(new ArrayList<SignMonthVO>(map.get(key).getMonthList().values()));
            list.add(userMonthListVO);
        }
        PageWrapper<UserMonthListVO> result = new  PageWrapper();
        BeanUtils.copy(page, result);
        result.setRecords(list);
        return result;
    }

    /**
     * 获取导出的签到信息列表
     * @param userAndDeptParam
     * @return
     */
    @Override
    public List<SignExcelVO> getSignInList(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper){
        String[] deptIds = StringUtils.split(userAndDeptParam.getDeptIds(),",");
        String[] userIdCs = StringUtils.split(userAndDeptParam.getUserIds(),",");
        ResponseEntityWrapper<List<SignUserInfoVO>> memResult = userRemoteService.findSubLists(deptIds, userIdCs);
        List<SignUserInfoVO> userList =  memResult.getData();
        if(userList.size() == 0) {
            return null;
        }
        Map<Long, SignUserInfoVO> map = new HashMap<>();
        List<Long>  ids = new ArrayList<>();
        Date startD = DateUtil.StringToDate(userAndDeptParam.getSignDate() + "-01", DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.getLastDayOfMonth(startD);
        int dayB = DateUtil.getIntervalDays(startD, endDate);
        UserMonthVO userMonthVO;
        SignMonthVO vo;
        HashMap obj;
        //接收rpc数据后拼装数据
        for(SignUserInfoVO objS : userList) {
            map.put(objS.getId(), objS);
            ids.add(objS.getId());
        }
        String userIds = StringUtils.join(ids, ",");
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserIds(userIds);
        signMapperParam.setStartDate(startD.getTime());
        signMapperParam.setEndDate(endDate.getTime());
        //查询明细数据后，组装
        List<SignExcelVO> list = mapper.querySignDetail(signMapperParam);
        for (SignExcelVO excelVO : list) {
            SignUserInfoVO signUserInfoVO = map.get(excelVO.getUserId());
            excelVO.setDeptName(StringUtils.join(signUserInfoVO.getDeptNames(), SignConstant.SEPARATE_STR));
            excelVO.setUserName(signUserInfoVO.getName());
            excelVO.setPosition(signUserInfoVO.getPosition());
            List<AttrValueVO> imgUrls = null;
            AttrValueVO attrValueVO;
            //图片地址
            if(StringUtils.isNotBlank(excelVO.getImgUrls())) {
                int index = 1;
                for (String img : StringUtils.split(excelVO.getImgUrls(), SignConstant.SEPARATE_STR)) {
                    imgUrls = new ArrayList<AttrValueVO>();
                    attrValueVO = new AttrValueVO();
                    attrValueVO.setAttrVal(img);
                    attrValueVO.setCkey("图" + (index++));
                    imgUrls.add(attrValueVO);
                }
            }
            excelVO.setListValue(imgUrls);
        }
        return list;
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
        List<SignExcelVO> exportData = getSignInList(userAndDeptParam, signDetailMapper);
        SignExModel signExModel = new SignExModel();

        List<ExcelModel> excelModelList = new ArrayList<>();
        ExcelModel excelModel = new ExcelModel();
        // 注入工作表名称
        excelModel.setSheetName("外出签到数据报表");

        excelModel.setNotice("外出"+ SignExConsts.NOTICE);
        // 注入文件名
        StringBuffer fileName = new StringBuffer().append("外出").append(SignExConsts.SHEET_NAME).append(SignExConsts.SEPARATOR_POINT).append(SignExConsts.Type_xls);
        excelModel.setFileName(fileName.toString());

        // 注入表头
        String statisticDate = "统计日期：";
        statisticDate = statisticDate + DateUtil.getDate(startD) + " —— " + DateUtil.getDate(endDate) + "       ";
        String TABLE_HEADER = "报表生成日期："+ DateUtil.getDate(new Date());
        excelModel.setTableHeader(statisticDate + TABLE_HEADER);

        // 注入数据项名称
        List<SignTemplVO> signTemplVOList = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
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
}
