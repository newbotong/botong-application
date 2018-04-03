package com.yunjing.botong.log.service.impl;

import com.yunjing.botong.log.service.LogReportService;
import com.yunjing.mommon.wrapper.PageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

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
public class LogReportServiceImpl implements LogReportService {


    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param memberId   用户所有企业的成员id
     * @param submitType 日报模版类型（0-全部 1-日报 2-周报 3-月报）
     * @param startDate  开始时间戳
     * @param endDate    结束时间戳
     * @return
     */
    @Override
    public PageWrapper query(long memberId, int submitType, long startDate, long endDate) {
        Criteria criteria = new Criteria();

        Query query = new Query().addCriteria(criteria);

        mongoTemplate.find(query, this.getClass());

        return null;
    }
}
