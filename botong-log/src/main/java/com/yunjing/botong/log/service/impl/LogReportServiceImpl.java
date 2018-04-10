package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.common.mongo.dao.Page;
import com.common.mongo.util.BeanUtils;
import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.dao.LogReportDao;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.LogReportService;
import com.yunjing.botong.log.util.ListPage;
import com.yunjing.botong.log.vo.LogDetailVO;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.botong.log.vo.MemberVO;
import com.yunjing.botong.log.vo.UserVO;
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
        boolean manager = appCenterService.isManager(String.valueOf(appId), String.valueOf(memberId), true);
        List<String> memberIdList = new ArrayList<>();
        if (manager) {
            // 管理员查询他所在企业的管理的memberId
            List<Member> manageScopeList = manageScopeList(memberId, appId);
            if (CollectionUtils.isEmpty(manageScopeList)) {
                throw new ParameterErrorException(StatusCode.NOT_ADMIN_AUTH);
            }
            for (Member member : manageScopeList) {
                memberIdList.add(member.getId());
            }
        } else {
            // 不是管理员查自己的
            memberIdList.add(memberId);
        }

        Map<String, MemberVO> userVOMap = new HashMap<>(16);
        List<Object> list = redisTemplate.opsForHash().multiGet(LogConstant.LOG_MEMBER_INFO, new HashSet<>(memberIdList));
        for (Object o : list) {
            MemberVO vo = JSON.parseObject(String.valueOf(o), MemberVO.class);
            userVOMap.put(vo.getId(), vo);
        }

        PageWrapper<LogDetail> report = logReportDao.report(pageNo, pageSize, orgId, memberIdList, submitType, startDate, endDate);
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (report.getRecords() != null && report.getSize() > 0) {
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            Set<Map.Entry<String, MemberVO>> entries = userVOMap.entrySet();
            for (LogDetail detail : report.getRecords()) {
                vo = BeanUtils.map(detail, LogDetailVO.class);
                for (Map.Entry<String, MemberVO> entry : entries) {
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

    @Override
    public PageWrapper<UserVO> submitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize) {
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
        Page<String> submitList = logReportDao.submitList(orgId, submitType, date, pageNo, pageSize, memberIdList);
        List<String> submitMemberIdList = submitList.getRows();
        List<UserVO> users = buildUserVo(list, submitMemberIdList);
        PageWrapper<UserVO> wrapper = new PageWrapper<>();
        wrapper.setRecords(users);
        wrapper.setSize(submitList.getSize());
        wrapper.setCurrent(submitList.getCurrent());
        wrapper.setPages(submitList.getPages());
        wrapper.setTotal(submitList.getTotal());
        return wrapper;
    }

    @Override
    public PageWrapper<UserVO> unSubmitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize) {
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
        List<UserVO> users = buildUserVo(list, memberIdList);
        ListPage<UserVO> page = new ListPage<>(users, pageSize);
        PageWrapper<UserVO> wrapper = new PageWrapper<>();
        wrapper.setRecords(page.getPagedList(pageNo));
        wrapper.setSize(page.getPageSize());
        wrapper.setCurrent(pageNo);
        wrapper.setPages(page.getPageCount());
        wrapper.setTotal(users.size());

        return wrapper;
    }

    private List<UserVO> buildUserVo(List<Member> list, List<String> memberIdList) {
        List<UserVO> users = new ArrayList<>();
        UserVO user;
        for (Member member : list) {
            for (String mId : memberIdList) {
                if (member.getCompanyId().equals(mId)) {
                    user = new UserVO();
                    user.setUserNick(member.getMemberName());
                    user.setMemberId(mId);
                    user.setProfile(member.getProfile());
                    user.setUserMobile(member.getMobile());
                    users.add(user);
                }
            }
        }
        return users;
    }


    /**
     * 管理成员id集合
     *
     * @param memberId
     * @param appId
     * @return
     */
    private List<Member> manageScopeList(String memberId, String appId) {
        // 根据memberId 查询管理范围
        return appCenterService.manageScope(memberId, appId);
    }
}
