package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.common.mongo.dao.Page;
import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.dao.LogReportDao;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.LogReportService;
import com.yunjing.botong.log.util.ListPage;
import com.yunjing.botong.log.vo.LogDetailVO;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * <p> 日志报表服务
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/3.
 */
@Slf4j
@Service
public class LogReportServiceImpl implements LogReportService {

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
    public PageWrapper<LogDetailVO> query(String memberId, String orgId, String appId, int pageNo, int pageSize, int submitType, long startDate, long endDate) {

        // 1. 校验是否是管理员
        boolean manager = appCenterService.isManager(appId, memberId, true);
        List<String> memberIdList = new ArrayList<>();
        if (manager) {
            // 管理员查询他所在企业的管理的memberId
            List<Member> list = manageScopeList(memberId, appId);
            if (CollectionUtils.isEmpty(list)) {
                throw new ParameterErrorException(StatusCode.NOT_ADMIN_AUTH);
            }
            for (Member member : list) {
                memberIdList.add(member.getId());
            }
        } else {
            // 不是管理员查自己的
            memberIdList.add(memberId);
        }

        Map<String, Member> userVOMap = new HashMap<>(16);
        List<Object> list = redisTemplate.opsForHash().multiGet(LogConstant.LOG_MEMBER_INFO, new HashSet<>(memberIdList));
        for (Object o : list) {
            Member vo = JSON.parseObject(String.valueOf(o), Member.class);
            userVOMap.put(vo.getId(), vo);
        }

        Page<LogDetail> report = logReportDao.report(pageNo, pageSize, orgId, memberIdList, submitType, startDate, endDate);
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (report != null) {
            List<LogDetail> rows = report.getRows();
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            Set<Map.Entry<String, Member>> entries = userVOMap.entrySet();
            for (LogDetail detail : rows) {
                vo = mapping(detail);
                for (Map.Entry<String, Member> entry : entries) {
                    if (detail.getMemberId().equals(entry.getKey())) {
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


    private LogDetailVO mapping(LogDetail detail) {
        LogDetailVO vo = new LogDetailVO();
        vo.setLogId(detail.getLogId());
        vo.setDeleteStatus(detail.getDeleteStatus());
        List<String> images = detail.getLogImages();
        String[] img = new String[images.size()];
        for (int i = 0; i < images.size(); i++) {
            img[i] = images.get(i);
        }
        vo.setLogImages(img);
        vo.setLogVersion(detail.getLogVersion());
        vo.setOrgId(detail.getOrgId());
        vo.setRemark(detail.getRemark());
        vo.setState(detail.getState());
        vo.setTemplateId(detail.getTemplateId());
        vo.setContents(detail.getContents());
        vo.setSubmitTime(detail.getSubmitTime().getTime());
        vo.setSubmitType(detail.getSubmitType());
        return vo;
    }

    @Override
    public PageWrapper<Member> submitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize) {
        // 获取管理范围集合
        List<Member> list = manageScopeList(memberId, appId);
        if (CollectionUtils.isEmpty(list)) {
            throw new ParameterErrorException(StatusCode.NOT_ADMIN_AUTH);
        }
        List<String> memberIdList = new ArrayList<>();
        for (Member member : list) {
            memberIdList.add(member.getId());
        }

        // 指定日期以提交列表
        Page<String> page = logReportDao.submitList(orgId, submitType, date, pageNo, pageSize, memberIdList);

        List<Member> members = buildData(list, page.getRows());

        PageWrapper<Member> wrapper = new PageWrapper<>();
        wrapper.setRecords(members);
        wrapper.setSize(page.getSize());
        wrapper.setCurrent(page.getCurrent());
        wrapper.setPages(page.getPages());
        wrapper.setTotal(page.getTotal());
        return wrapper;
    }

    private List<Member> buildData(List<Member> list, List<String> rows) {
        List<Member> members = new ArrayList<>();
        for (String mId : rows) {
            for (Member member : list) {
                if (member.getId().equals(mId)) {
                    members.add(member);
                    break;
                }
            }
        }
        return members;
    }

    @Override
    public PageWrapper<Member> unSubmitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize) {
        // 获取管理范围集合
        List<Member> list = manageScopeList(memberId, appId);
        if (CollectionUtils.isEmpty(list)) {
            throw new ParameterErrorException(StatusCode.NOT_ADMIN_AUTH);
        }

        List<String> memberIdList = new ArrayList<>();
        for (Member member : list) {
            memberIdList.add(member.getId());
        }

        // 指定日期所有已提交列表
        List<String> submitList = logReportDao.submitList(orgId, submitType, date, memberIdList);

        // 取交集
        Collection collection = CollectionUtils.intersection(memberIdList, submitList);

        // 去除已提交列表，其余为未提交列表
        memberIdList.removeAll(collection);

        ListPage<Member> page = new ListPage<>(list, pageSize);
        PageWrapper<Member> wrapper = new PageWrapper<>();
        wrapper.setRecords(page.getPagedList(pageNo));
        wrapper.setSize(page.getPageSize());
        wrapper.setCurrent(pageNo);
        wrapper.setPages(page.getPageCount());
        wrapper.setTotal(list.size());

        return wrapper;
    }


    /**
     * 管理成员id集合
     *
     * @param appId
     * @param memberId
     * @return
     */
    private List<Member> manageScopeList(String memberId, String appId) {
        // 根据memberId 查询管理范围
        return appCenterService.manageScope(appId, memberId);
    }
}
