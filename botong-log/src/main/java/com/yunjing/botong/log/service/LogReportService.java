package com.yunjing.botong.log.service;

import com.yunjing.mommon.wrapper.PageWrapper;

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
     * @param submitType 日报模版类型（0-全部 1-日报 2-周报 3-月报）
     * @param startDate  开始时间戳
     * @param endDate    结束时间戳
     * @return
     */
    PageWrapper query(long memberId, int submitType, long startDate, long endDate);

}
