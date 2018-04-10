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
                // throw new ParameterErrorException(StatusCode.NOT_ADMIN_AUTH);
                list = new ArrayList<>();
            }

            // TODO 测试数据
            Member member1 = new Member();
            member1.setCompanyId("6387137356046012416");
            member1.setPassportId("789456123");
            member1.setMemberName("shsaksa");
            member1.setMobile("15546789243");
            member1.setId("6386821498899795968");
            list.add(member1);


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

        PageWrapper<LogDetail> report = logReportDao.report(pageNo, pageSize, orgId, memberIdList, submitType, startDate, endDate);
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (report != null && CollectionUtils.isNotEmpty(report.getRecords())) {
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            Set<Map.Entry<String, Member>> entries = userVOMap.entrySet();
            for (LogDetail detail : report.getRecords()) {
                vo = BeanUtils.map(detail, LogDetailVO.class);
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

    @Override
    public PageWrapper<Member> submitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize) {
        // 获取管理范围集合
        List<Member> list = manageScopeList(memberId, appId);
        if (CollectionUtils.isEmpty(list)) {
            // throw new ParameterErrorException(StatusCode.NOT_ADMIN_AUTH);

            // TODO 测试数据
            list = buildTestMemberData();
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
            // throw new ParameterErrorException(StatusCode.NOT_ADMIN_AUTH);

            // TODO 测试数据
            list = buildTestMemberData();
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
     * @param memberId
     * @param appId
     * @return
     */
    private List<Member> manageScopeList(String memberId, String appId) {
        // 根据memberId 查询管理范围
        return appCenterService.manageScope(appId, memberId);
    }


    /**
     * 测试数据
     *
     * @return
     */
    private List<Member> buildTestMemberData() {
        List<Member> list = new ArrayList<>();
        list.add(new Member("6387252886685880323", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216520023379968", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387252886685880324", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216520027574275", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387252886685880320", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388997372029964288", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216520027574274", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216520027574273", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387252886685880321", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387221054862921732", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387252886685880322", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216520027574272", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6386505038969180166", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388997328186904576", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388996885952073728", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387221054862921730", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216817735077891", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387221054862921731", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6389047773366325248", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216817735077890", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388997260423729152", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388916729069703168", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388916729069703169", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6386821498899795968", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6386837899156918272", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388997346864140288", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6389045655553183744", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388558948298919938", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388558948298919939", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387213507254816768", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388558948298919936", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387221054862921729", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388558948298919937", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216817735077888", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387221054862921728", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216817735077889", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388916729069703171", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388997399360049152", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6386837371463143424", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388916729069703170", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388997068404297728", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388916729069703172", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6386837057284608000", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388558948298919940", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6387216817730883584", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6386824524179968000", "这是名字", "这是电话", "这是头像"));
        list.add(new Member("6388627626654699520", "这是名字", "这是电话", "这是头像"));
        return list;
    }
}
