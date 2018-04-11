package com.yunjing.botong.log.dao;

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
import java.util.ArrayList;
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


    /**
     * 根据时间分页查询指定日志已提交的列表
     *
     * @param orgId
     * @param submitType
     * @param date
     * @param pageNo
     * @param pageSize
     * @param memberIdList
     * @return
     */
    public Page<String> submitList(String orgId, int submitType, String date, int pageNo, int pageSize, List<String> memberIdList) {
        Criteria criteria = Criteria.where("orgId").is(orgId);
        criteria.and("memberId").in(memberIdList);
        if (submitType != 0) {
            criteria.and("submitType").is(submitType);
        }
        Query query = new Query(criteria);
        buildQueryDate(query, date);

        // 根据时间查询指定日志已提交的列表
        Page<LogDetail> page = findPage(new Page<>(pageNo, pageSize), query);

        List<String> list = new ArrayList<>();
        Page<String> submitMemberIdPage = new Page<>();
        for (LogDetail detail : page.getRows()) {
            list.add(String.valueOf(detail.getMemberId()));
        }

        submitMemberIdPage.setRows(list);
        submitMemberIdPage.setCurrent(page.getCurrent());
        submitMemberIdPage.setPages(page.getPages());
        submitMemberIdPage.setSize(page.getSize());
        submitMemberIdPage.setTotal(page.getTotal());

        return submitMemberIdPage;
    }

    /**
     * 根据时间查询指定日志已提交的列表
     *
     * @param orgId
     * @param submitType
     * @param date
     * @param memberIdList
     * @return
     */
    public List<String> submitList(String orgId, int submitType, String date, List<String> memberIdList) {
        Criteria criteria = Criteria.where("orgId").is(orgId);
        criteria.and("memberId").in(memberIdList);
        if (submitType != 0) {
            criteria.and("submitType").is(submitType);
        }
        Query query = new Query(criteria);
        buildQueryDate(query, date);

        List<String> list = new ArrayList<>();
        List<LogDetail> details = find(query);

        for (LogDetail detail : details) {
            list.add(String.valueOf(detail.getMemberId()));
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
            Date end = DateUtils.parseDate(date + " 23:23:59", "yyyy-MM-dd HH:mm:ss");
            query.addCriteria(Criteria.where("submitTime").gte(start).lte(end));
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
        return findPage(new Page<>(pageNo, pageSize), query);
    }
}
