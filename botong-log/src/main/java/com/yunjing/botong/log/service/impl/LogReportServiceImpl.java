package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.common.mongo.dao.Page;
import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.cache.MemberRedisOperator;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.dao.LogReportDao;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.LogReportService;
import com.yunjing.botong.log.util.ListPage;
import com.yunjing.botong.log.vo.LogDetailVO;
import com.yunjing.botong.log.vo.ManagerMemberInfoVo;
import com.yunjing.botong.log.vo.Member;
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


    @Autowired
    private MemberRedisOperator redisOperator;

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

        log.info("日志报表统计参数：memberId={},orgId={},appId={}", memberId, orgId, appId);

        // 1. 校验是否是管理员
        boolean manager = appCenterService.isManager(appId, memberId, true);
        List<String> memberIdList = new ArrayList<>();
        if (manager) {
            // 管理员查询他所在企业的管理的memberId
            List<Member> list = manageScopeList(memberId, appId);
            if (list == null) {
                list = new ArrayList<>();
            }
            if (CollectionUtils.isEmpty(list)) {
                memberIdList.add(memberId);
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
            if (vo != null) {
                userVOMap.put(vo.getId(), vo);
            }
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
        if (CollectionUtils.isNotEmpty(images)) {
            String[] img = new String[images.size()];
            for (int i = 0; i < images.size(); i++) {
                img[i] = images.get(i);
            }
            vo.setLogImages(img);
        }
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
    public PageWrapper<ManagerMemberInfoVo> submitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize) {
        // 指定时间已提交列表
        List<Member> members = manageScope(memberId, orgId, appId, submitType, date);
        return buildWrapper(members, pageNo, pageSize);
    }


    @Override
    public PageWrapper<ManagerMemberInfoVo> unSubmitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize) {
        // 获取管理范围集合
        List<Member> list = manageScopeList(memberId, appId);
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        // 加上自己的
        list.add(redisOperator.getMember(memberId));
        // 管理的成员id集合
        Set<String> memberIdList = new HashSet<>();
        for (Member member : list) {
            memberIdList.add(member.getId());
        }

        // 已提交成员集合
        Set<String> submitList = new HashSet<>();
        // 指定日期所有已提交列表
        List<Member> members = manageScope(memberId, orgId, appId, submitType, date);
        for (Member member : members) {
            submitList.add(member.getId());
        }

        // 取交集
        Collection collection = CollectionUtils.intersection(memberIdList, submitList);

        // 去除已提交列表，其余为未提交列表
        memberIdList.removeAll(collection);

        members = redisOperator.getMemberList(memberIdList);

        return buildWrapper(members, pageNo, pageSize);
    }


    /**
     * members 分页
     *
     * @param members
     * @param pageNo
     * @param pageSize
     * @return
     */
    private PageWrapper<ManagerMemberInfoVo> buildWrapper(List<Member> members, int pageNo, int pageSize) {
        List<ManagerMemberInfoVo> infoVos = com.yunjing.mommon.utils.BeanUtils.mapList(members, ManagerMemberInfoVo.class);

        PageWrapper<ManagerMemberInfoVo> wrapper = new PageWrapper<>();
        if (CollectionUtils.isNotEmpty(infoVos)) {
            ListPage<ManagerMemberInfoVo> page = new ListPage<>(infoVos, pageSize);
            wrapper.setRecords(page.getPagedList(pageNo));
            wrapper.setSize(page.getPageSize());
            wrapper.setCurrent(pageNo);
            wrapper.setPages(page.getPageCount());
            wrapper.setTotal(infoVos.size());
        }
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

    /**
     * 管理范围下指定日期提交列表
     *
     * @param memberId
     * @param orgId
     * @param appId
     * @param submitType
     * @param date
     * @return
     */
    private List<Member> manageScope(String memberId, String orgId, String appId, int submitType, String date) {
        List<Member> members = appCenterService.manageScope(appId, memberId);
        if (CollectionUtils.isEmpty(members)) {
            members = new ArrayList<>();
        }
        // 根据memberId获取member信息
        Member member = redisOperator.getMember(memberId);
        members.add(member);


        List<String> memberIdList = new ArrayList<>();
        for (Member m : members) {
            memberIdList.add(m.getId());
        }

        // 指定日期所有已提交列表
        Set<String> submitMemberIdList = logReportDao.submitList(orgId, submitType, date, memberIdList);

        return redisOperator.getMemberList(submitMemberIdList);
    }
}
