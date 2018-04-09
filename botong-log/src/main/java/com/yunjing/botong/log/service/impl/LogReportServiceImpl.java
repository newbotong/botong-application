package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.common.mongo.util.BeanUtils;
import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.dao.LogReportDao;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.processor.okhttp.impl.AppCenterServiceImpl;
import com.yunjing.botong.log.service.LogReportService;
import com.yunjing.botong.log.vo.LogDetailVO;
import com.yunjing.botong.log.vo.MemberInfo;
import com.yunjing.botong.log.vo.MemberVO;
import com.yunjing.botong.log.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/3.
 */
@Slf4j
@Service
public class LogReportServiceImpl implements LogReportService, AppCenterService.OrgMemberCallback, AppCenterService.VerifyManagerCallback {

    /**
     * mongo 数据中心
     */
    @Autowired
    private LogReportDao logReportDao;

    /**
     * 应用中心
     */
    @Autowired
    private AppCenterService appCenterService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        AppCenterServiceImpl appCenterService = (AppCenterServiceImpl) this.appCenterService;
        appCenterService.setOrgMemberCallback(this);
        appCenterService.setVerifyManagerCallback(this);
    }

    /**
     * 日志报表统计
     *
     * @param memberId   用户所有企业的成员id
     * @param submitType 日报模版类型（0-全部 1-日报 2-周报 3-月报）
     * @param startDate  开始时间戳
     * @param endDate    结束时间戳
     * @return
     */
    @Override
    public PageWrapper<LogDetailVO> query(long memberId, long orgId, long appId, int pageNo, int pageSize, int submitType, long startDate, long endDate) {

        // 1. 校验是否是管理员
        boolean manager = appCenterService.isManager(String.valueOf(appId), String.valueOf(memberId), true);
        List<Long> memberIdList = new ArrayList<>();
        if (manager) {
            // 管理员查询他所在企业的管理的memberId

        } else {
            // 不是管理员查自己的
            memberIdList.add(memberId);
        }

        Set<Object> set = new HashSet<>();
        if (CollectionUtils.isNotEmpty(memberIdList)) {
            for (Long mId : memberIdList) {
                set.add(String.valueOf(mId));
            }
        }

        Map<Long, UserVO> userVOMap = new HashMap<>(16);
        List<Object> list = redisTemplate.opsForHash().multiGet(LogConstant.LOG_MEMBER_INFO, set);
        UserVO userVO;
        for (Object o : list) {
            MemberVO vo = JSON.parseObject(String.valueOf(o), MemberVO.class);
            userVO = new UserVO();
            userVO.setMemberId(Long.parseLong(vo.getId()));
            userVO.setUserMobile(vo.getMobile());
            userVO.setProfile(vo.getProfile());
            userVO.setUserNick(vo.getName());
            userVOMap.put(userVO.getMemberId(), userVO);
        }

        PageWrapper<LogDetail> report = logReportDao.report(pageNo, pageSize, orgId, memberIdList, submitType, startDate, endDate);
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (report.getRecords() != null && report.getSize() > 0) {
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            Set<Map.Entry<Long, UserVO>> entries = userVOMap.entrySet();
            for (LogDetail detail : report.getRecords()) {
                vo = BeanUtils.map(detail, LogDetailVO.class);
                for (Map.Entry<Long, UserVO> entry : entries) {
                    if (detail.getMemberId().longValue() == entry.getKey().longValue()) {
                        vo.setUser(entry.getValue());
                        break;
                    }
                }
                resultRecord.add(vo);
            }
            result.setCurrent(report.getCurrent());
            result.setPages(report.getPages());
            result.setSize(report.getSize());
            result.setTotal(report.getTotal());
            result.setRecords(resultRecord);
        }
        return result;
    }

    @Override
    public void result(List<MemberInfo> infos) {

    }

    @Override
    public void verify(boolean isManager) {

    }
}
