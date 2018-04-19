package com.yunjing.botong.log.dao;

import com.alibaba.fastjson.JSON;
import com.common.mongo.dao.Page;
import com.common.mongo.dao.impl.BaseMongoDaoImpl;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    /**
     * 根据时间查询指定日志已提交的列表
     *
     * @param orgId
     * @param submitType
     * @param date
     * @param memberIdList
     * @return
     */
    public Set<String> submitList(String orgId, int submitType, String date, List<String> memberIdList) {
        Criteria criteria = Criteria.where("orgId").is(orgId);
        criteria.and("memberId").in(memberIdList);
        if (submitType != 0) {
            criteria.and("submitType").is(submitType);
        }
        Query query = new Query(criteria);
        buildQueryDate(query, date);

        Set<String> list = new HashSet<>();
        List<LogDetail> details = find(query);

        for (LogDetail detail : details) {
            list.add(detail.getMemberId());
        }
        return list;
    }

    /**
     * 查询日期转换
     *
     * @param query
     * @param date
     */
    private void buildQueryDate(Query query, String date) {
        try {
            Date start = DateUtils.parseDate(date, "yyyy-MM-dd");
            Date end = DateUtils.parseDate(date + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            query.addCriteria(Criteria.where("submitTime").gte(start).lte(end));
            log.info("日志管理列表统计查询参数：{}", JSON.toJSONString(query));
        } catch (ParseException e) {
            throw new ParameterErrorException("日期格式错误，请传入(yyyy-MM-dd)");
        }
    }


    /**
     * 日志报表统计
     *
     * @param pageNo
     * @param pageSize
     * @param orgId
     * @param memberId
     * @param submitType
     * @param startDate
     * @param endDate
     * @return
     */
    public Page<LogDetail> report(int pageNo, int pageSize, String orgId, List<String> memberId, int submitType, long startDate, long endDate) {
        Criteria criteria = Criteria.where("orgId").is(orgId);
        criteria.and("memberId").in(memberId);
        criteria.and("submitType").is(submitType);

        if (startDate != 0 && endDate != 0) {
            String start = DateFormatUtils.format(new Date(startDate), "yyyy-MM-dd") + " 00:00:00";
            String end = DateFormatUtils.format(new Date(endDate), "yyyy-MM-dd") + " 23:59:59";

            try {
                Date s = DateUtils.parseDate(start, "yyyy-MM-dd HH:mm:ss");
                Date e = DateUtils.parseDate(end, "yyyy-MM-dd HH:mm:ss");
                criteria.andOperator(Criteria.where("submitTime").lte(e).gte(s));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "submitTime"));
        log.info("日志报表统计查询参数：{}", JSON.toJSONString(query));
        return findPage(new Page<>(pageNo, pageSize), query);
    }
}
