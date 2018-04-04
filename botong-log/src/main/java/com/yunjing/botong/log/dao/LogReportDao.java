package com.yunjing.botong.log.dao;

import com.common.mongo.dao.Page;
import com.common.mongo.dao.impl.BaseMongoDaoImpl;
import com.common.mongo.util.BeanUtils;
import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.entity.LogDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * <p> 日志报表
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/4.
 */
@Slf4j
@Service
public class LogReportDao extends BaseMongoDaoImpl<LogDetail> {


    public PageWrapper<LogDetail> report(int pageNo, int pageSize, long orgId, List<Long> memberId, int submitType, long startDate, long endDate) {
        Page<LogDetail> page = new Page<>(pageNo, pageSize);
        Criteria criteria = Criteria.where("orgId").is(orgId);
        criteria.and("memberId").in(memberId);

        if (submitType != 0) {
            criteria.and("submitType").is(submitType);
        }

        if (startDate != 0 && endDate != 0) {
            String start = DateFormatUtils.format(new Date(startDate), "yyyy-MM-dd HH:mm:ss");
            String end = DateFormatUtils.format(new Date(endDate), "yyyy-MM-dd HH:mm:ss");
            criteria.andOperator(Criteria.where("submitTime").lte(end).gte(start));
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "submitTime"));
        PageWrapper<LogDetail> pageWrapper = BeanUtils.mapPage(findPage(page, query), LogDetail.class);
        return pageWrapper;
    }

}
