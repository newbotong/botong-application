package com.yunjing.botong.log.service.impl;

import com.common.mongo.util.BeanUtils;
import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.dao.LogReportDao;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.processor.okhttp.impl.AppCenterServiceImpl;
import com.yunjing.botong.log.service.LogReportService;
import com.yunjing.botong.log.vo.LogDetailVO;
import com.yunjing.botong.log.vo.MemberInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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

        PageWrapper<LogDetail> report = logReportDao.report(pageNo, pageSize, orgId, memberIdList, submitType, startDate, endDate);
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (report.getRecords() != null && report.getSize() > 0) {
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            for (LogDetail detail : report.getRecords()) {

                vo = BeanUtils.map(detail, LogDetailVO.class);
                // vo.setUser();


                resultRecord.add(vo);
            }

            result.setCurrent(report.getCurrent());
            result.setPages(report.getPages());
            result.setSize(report.getSize());
            result.setTotal(report.getTotal());
            result.setRecords(resultRecord);
        }


        return null;
    }

    @Override
    public void result(List<MemberInfo> infos) {

    }

    @Override
    public void verify(boolean isManager) {

    }
}
