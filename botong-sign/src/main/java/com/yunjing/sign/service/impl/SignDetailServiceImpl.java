package com.yunjing.sign.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.Enum.DateStyle;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.sign.beans.model.SignDetail;
import com.yunjing.sign.beans.model.SignDetailImg;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.SignMapperParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.*;
import com.yunjing.sign.constant.SignConstant;
import com.yunjing.sign.dao.mapper.SignDetailMapper;
import com.yunjing.sign.service.ISignDetailImgService;
import com.yunjing.sign.service.ISignDetailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
    public SignListVO getCountInfo(UserAndDeptParam userAndDeptParam) {
        List userList = new ArrayList();
        Map<Long, SignUserInfoVO> map = new HashMap<>();
        List<Long>  ids = new ArrayList<>();
        for(Object o : userList) {
            SignUserInfoVO obj = (SignUserInfoVO) o;
            map.put(obj.getUserId(), obj);
            ids.add(obj.getUserId());
        }
        String userIds = StringUtils.join(ids, ",");
        long startDate = DateUtil.stringToDate(userAndDeptParam.getSignDate()).getTime();
        long endDate = DateUtil.addDay(DateUtil.stringToDate(userAndDeptParam.getSignDate()), 1).getTime();
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserIds(userIds);
        signMapperParam.setStartDate(startDate);
        signMapperParam.setEndDate(endDate);
        List<SignUserInfoVO> userIdList = signDetailMapper.getCountInfo(signMapperParam);
        List<SignUserInfoVO> signList = new ArrayList<>();
        List<SignUserInfoVO> unSignList = new ArrayList<>();

        for(SignUserInfoVO vo : userIdList) {
            map.get(vo.getUserId()).setSignState(1);
            signList.add(map.get(vo.getUserId()));
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
    public MySignVO queryMonthInfo(SignDetailParam signDetailParam) {
        SignMapperParam signMapperParam = new SignMapperParam();
        signMapperParam.setUserId(signDetailParam.getUserId());
        Date startD = DateUtil.StringToDate(signDetailParam.getSignDate(), DateStyle.YYYY_MM);
        Long startDate = startD.getTime();
        long endDate = DateUtil.getLastDayOfMonth(startD).getTime();
        signMapperParam.setStartDate(startDate);
        signMapperParam.setEndDate(endDate);
        List<SignDetailVO> signList = signDetailMapper.queryMonthInfo(signMapperParam);
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

        return null;
    }
}
