package com.yunjing.botong.log.service;

import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.vo.LogDetailVO;
import com.yunjing.botong.log.vo.Member;

/**
 * <p>
 * <p> 日志报表服务
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/3.
 */
public interface LogReportService {


    /**
     * 日志报表统计
     *
     * @param memberId   用户所有企业的成员id
     * @param orgId      企业编号
     * @param appId      应用id
     * @param pageNo     页码
     * @param pageSize   页大小
     * @param submitType 日报模版类型（1-日报 2-周报 3-月报）
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return
     */
    PageWrapper<LogDetailVO> query(String memberId, String orgId, String appId, int pageNo, int pageSize, int submitType, long startDate, long endDate);


    /**
     * 日志管理列表(已提交)
     *
     * @param memberId   管理员id
     * @param orgId      组织机构id
     * @param appId      appid
     * @param submitType 提交类型（1-日报 2-周报 3-月报）
     * @param date       时间
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return
     */
    PageWrapper<Member> submitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize);

    /**
     * 日志管理列表（未提交）
     *
     * @param memberId   管理员id
     * @param orgId      组织机构id
     * @param appId      appid
     * @param submitType 提交类型（1-日报 2-周报 3-月报）
     * @param date       时间
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return
     */
    PageWrapper<Member> unSubmitList(String memberId, String orgId, String appId, int submitType, String date, int pageNo, int pageSize);

}
