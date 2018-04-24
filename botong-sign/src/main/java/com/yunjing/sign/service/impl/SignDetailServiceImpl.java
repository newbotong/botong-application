package com.yunjing.sign.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.common.redis.share.UserInfo;
import com.yunjing.mommon.Enum.DateStyle;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.model.SignConfigModel;
import com.yunjing.sign.beans.model.SignDetail;
import com.yunjing.sign.beans.model.SignDetailImg;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.SignMapperParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.*;
import com.yunjing.sign.cache.MemberRedisOperator;
import com.yunjing.sign.constant.SignConstant;
import com.yunjing.sign.dao.SignBaseMapper;
import com.yunjing.sign.dao.mapper.SignDetailMapper;
import com.yunjing.sign.excel.BaseExModel;
import com.yunjing.sign.excel.ExcelModel;
import com.yunjing.sign.excel.SignExConsts;
import com.yunjing.sign.excel.SignExModel;
import com.yunjing.sign.processor.feign.UserRemoteService;
import com.yunjing.sign.processor.okhttp.UserRemoteApiService;
import com.yunjing.sign.service.ISignDetailImgService;
import com.yunjing.sign.service.ISignDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 签到明细 服务实现类
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@Slf4j
@Service
public class SignDetailServiceImpl extends ServiceImpl<SignDetailMapper, SignDetail> implements ISignDetailService {

    @Autowired
    private ISignDetailImgService iSignDetailImgService;

    @Autowired
    private SignDetailMapper signDetailMapper;

    @Autowired
    private UserRemoteApiService userRemoteApiService;

    @Autowired
    MemberRedisOperator memberRedisOperator;

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
        SignDetail signDetail = BeanUtils.map(signDetailParam, SignDetail.class);
        signDetail.setId(IDUtils.uuid());
        signDetail.insert();
        result.setUserId(signDetail.getUserId());
        result.setSignDate(signDetail.getCreateTime());
        if (StringUtils.isNotBlank(signDetailParam.getImgUrls())) {
            SignDetailImg detailImg;
            int i = 1;
            //构建图片对象
            List<SignDetailImg> list = new ArrayList<>();
            for (String imgUrl : signDetailParam.getImgUrls().split(SignConstant.SEPARATE_STR)) {
                detailImg = new SignDetailImg();
                detailImg.setSignDetailId(signDetail.getId());
                detailImg.setId(IDUtils.uuid());
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
     * @return                  签到对象
     */
    @Override
    public SignListVO getCountInfo(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper) {
        //okhttp 查询成员列表
        List<SignUserInfoVO> userList = getUsersList(userAndDeptParam);
        if(userList == null || userList.isEmpty()) {
            return null;
        }
        Map<String, SignUserInfoVO> map = new HashMap<>(userList.size());
        //组装map，方便业务对象直接根据memberId获取
        List<String>  ids = new ArrayList<>();
        for(SignUserInfoVO obj : userList) {
            map.put(obj.getMemberId(), obj);
            ids.add(obj.getMemberId());
        }
        //拼接成员Id查询
        String userIds = SignConstant.QUOTES_STR + StringUtils.join(ids, SignConstant.SEPARATE_QUOTES_COMMA_STR) + SignConstant.QUOTES_STR;
        long startDate = DateUtil.stringToDate(userAndDeptParam.getSignDate()).getTime();
        long endDate = DateUtil.addDay(DateUtil.stringToDate(userAndDeptParam.getSignDate()), 1).getTime();
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserIds(userIds);
        signMapperParam.setStartDate(startDate);
        signMapperParam.setEndDate(endDate);
        List<SignUserInfoVO> userIdList = mapper.getCountInfo(signMapperParam);
        List<SignUserInfoVO> signList = new ArrayList<>();
        List<SignUserInfoVO> unSignList = new ArrayList<>();
        //组装已经签到的成员列表
        for(SignUserInfoVO vo : userIdList) {
            map.get(vo.getMemberId()).setSignState(1);
            signList.add(map.get(vo.getMemberId()));
        }
        //组装未签到的成员列表
        for (String key : map.keySet()) {
            if (map.get(key).getSignState() == null || map.get(key).getSignState() != 1) {
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
     * @return                我的签到对象
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
        //组装map结构，根据日期为key，签到次数对象实体
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
        //人员和签到明细拼接组装列表
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
     * @param userAndDeptParam  部门和人员
     * @return                  分页对象
     */
    @Override
    public PageWrapper<UserMonthListVO> staticsMonthInfo(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper) {
        PageWrapper<SignUserInfoVO> page = new PageWrapper<>();
        if (StringUtils.isEmpty(userAndDeptParam.getDeptIds()) && StringUtils.isEmpty(userAndDeptParam.getUserIds())) {
            List<SignUserInfoVO> memberList = userRemoteApiService.manageScope(userAndDeptParam.getAppId(), userAndDeptParam.getMemberId());
            if (memberList == null) {
                return null;
            }
            Page<SignUserInfoVO> pageM = new Page<>(userAndDeptParam.getPageNo(), userAndDeptParam.getPageSize());
            pageM.setTotal(memberList!= null ? memberList.size() : SignConstant.BOTONG_ZERO_VALUE);
            int endIndex = pageM.getOffset() + pageM.getSize();
            if (endIndex > memberList.size()) {
                endIndex = memberList.size() - 1;
            }
            List<SignUserInfoVO> memList = memberList.subList(pageM.getOffset(), endIndex);
            pageM.setRecords(memList);
            page = BeanUtils.mapPage(pageM, SignUserInfoVO.class);
        } else {
            String[] deptIds = StringUtils.split(userAndDeptParam.getDeptIds(),",");
            String[] userIdCs = StringUtils.split(userAndDeptParam.getUserIds(),",");
            //okhttp 分页查询人员列表
            page  = userRemoteApiService.findMemberPage(deptIds, userIdCs, userAndDeptParam.getPageNo(), userAndDeptParam.getPageSize());
            //判断是否为空
        }

        List<SignUserInfoVO> userList;
        if (page != null) {
            userList = page.getRecords();
            if(userList == null || userList.isEmpty()) {
                return null;
            }
        } else {
            return null;
        }
        /**
         * 1、根据用户明细先组装人员对应的整月日期的对象，放入map中
         * 2、查询签到明细列表
         * 3、map和签到明细列表组装完整数据
         */
        Map<String, UserMonthVO> map = new HashMap<>(userList.size());
        List<String>  ids = new ArrayList<>();
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
                vo.setSignCount(SignConstant.BOTONG_ZERO_VALUE);
                vo.setUserId(obj.getMemberId());
                monthList.put(DateUtil.getDate(dDate), vo);
            }
            userMonthVO.setMonthList(monthList);
            map.put(obj.getMemberId(), userMonthVO);
            if(obj.getMemberId() != null) {
                ids.add(obj.getMemberId());
            }
        }
        String userIds = SignConstant.QUOTES_STR + StringUtils.join(ids, SignConstant.SEPARATE_QUOTES_COMMA_STR) + SignConstant.QUOTES_STR;
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserIds(userIds);
        signMapperParam.setStartDate(startD.getTime());
        signMapperParam.setEndDate(endDate.getTime());
        List<SignMonthVO> userIdList = mapper.staticsMonthInfo(signMapperParam);
        for(SignMonthVO vo1 : userIdList) {
            map.get(vo1.getUserId()).getMonthList().get(vo1.getSignDate()).setSignCount(vo1.getSignCount());
        }
        List<UserMonthListVO> list = new ArrayList<UserMonthListVO>();
        UserMonthListVO userMonthListVO;
        for (String key : map.keySet()) {
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
     * 根据条件查询人员列表
     * @param searchParam       参数
     * @return                  人员列表
     */
    private List<SignUserInfoVO> getUsersList(UserAndDeptParam searchParam) {
        List<SignUserInfoVO> memList = new ArrayList<>();
        //如果没有选择范围
        if (StringUtils.isEmpty(searchParam.getDeptIds()) && StringUtils.isEmpty(searchParam.getUserIds())) {
            if (StringUtils.isNotEmpty(searchParam.getOrgId())) {
                // 查询该企业下面的所有人
                memList = userRemoteApiService.findAllOrgMember(searchParam.getOrgId());
            }
        } else {
            //选择了发送人范围
            String[] deptIds = StringUtils.split(searchParam.getDeptIds(), SignConstant.SEPARATE_STR);
            String[] userIds = StringUtils.split(searchParam.getUserIds(), SignConstant.SEPARATE_STR);
            memList = userRemoteApiService.findSubLists(deptIds, userIds);
        }
        if (!memList.isEmpty()) {
            Set<Object> memberIds = new HashSet<>();
            for (SignUserInfoVO vo : memList) {
                memberIds.add(vo.getMemberId());
            }
            memList = memberRedisOperator.getMemberList(memberIds);
        }
        return memList;
    }




    /**
     * 获取导出的签到信息列表
     * @param userAndDeptParam  人员和部门对象
     * @param  mapper           对应的mapper
     * @return                  excel对象列表
     */
    @Override
    public List<SignExcelVO> getSignInList(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper){
        List<SignUserInfoVO> userList = new ArrayList<>();
        if (StringUtils.isEmpty(userAndDeptParam.getDeptIds()) && StringUtils.isEmpty(userAndDeptParam.getUserIds())) {
            userList = userRemoteApiService.manageScope(userAndDeptParam.getAppId(), userAndDeptParam.getMemberId());
        } else {
            String[] deptIds = StringUtils.split(userAndDeptParam.getDeptIds(),",");
            String[] userIdCs = StringUtils.split(userAndDeptParam.getUserIds(),",");
            //okhttp 查询成员列表
            userList = userRemoteApiService.findSubLists(deptIds, userIdCs);
        }

        if(userList == null || userList.size() == 0) {
            return null;
        }
        Map<String, SignUserInfoVO> map = new HashMap<>(userList.size());
        List<String>  ids = new ArrayList<>();
        Date startD = DateUtil.StringToDate(userAndDeptParam.getSignDate() + "-01", DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.getLastDayOfMonth(startD);
        int dayB = DateUtil.getIntervalDays(startD, endDate);
        UserMonthVO userMonthVO;
        SignMonthVO vo;
        HashMap obj;
        //接收rpc数据后拼装数据
        for(SignUserInfoVO objS : userList) {
            map.put(objS.getMemberId(), objS);
            ids.add(objS.getMemberId());
        }
        String userIds = SignConstant.QUOTES_STR + StringUtils.join(ids, SignConstant.SEPARATE_QUOTES_COMMA_STR) + SignConstant.QUOTES_STR;
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserIds(userIds);
        signMapperParam.setStartDate(startD.getTime());
        signMapperParam.setEndDate(endDate.getTime());
        //查询明细数据后，组装excel对象
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
     * 获取所有的签到明细
     *
     * @param signDetailParam   签到对象
     * @return                  签到明细列表
     */
    @Override
    public List<SignDetail> queryDetailList(SignDetailParam signDetailParam) {
        Date nowStart = DateUtil.stringToDate(signDetailParam.getSignDate());
        Date nowEnd = DateUtil.addDay(nowStart, 1);
        List<SignDetail> list = new SignDetail().selectList(new EntityWrapper<SignDetail>().eq("user_id", signDetailParam.getUserId()).lt("create_time", nowEnd.getTime()).ge("create_time", nowStart.getTime()));
        return list;
    }

    /**
     * 获取导出模板
     *
     * @param userAndDeptParam  部门和人员对象
     * @return                  excel文档对象
     */
    @Override
    public BaseExModel createTempExcel(UserAndDeptParam userAndDeptParam) {
        Date startD = DateUtil.StringToDate(userAndDeptParam.getSignDate() + "-01", DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.addDay(DateUtil.getLastDayOfMonth(startD), 1);
        List<SignExcelVO> exportData = getSignInList(userAndDeptParam, signDetailMapper);
        if (exportData == null) {
            exportData = new ArrayList<>();
        }
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
}
