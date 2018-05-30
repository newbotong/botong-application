package com.yunjing.botong.log.dao;

import com.common.mongo.dao.Page;
import com.common.mongo.dao.impl.BaseMongoDaoImpl;
import com.common.mongo.util.BeanUtils;
import com.common.mongo.util.PageWrapper;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import com.yunjing.botong.log.constant.LogConstant;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.mommon.enums.DateStyle;
import com.yunjing.mommon.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/3/27 9:13
 * @description:
 */
@Slf4j
@Component(value = "logDetailDao")
public class LogDetailDao extends BaseMongoDaoImpl<LogDetail> {

    /**
     * 查询我发送或者我收到的列表，1为我收到的，0为我发送的
     * @param pageNo        页码
     * @param pageSize      每页显示条数
     * @param orgId         企业id
     * @param userId        用户id
     * @param readStatus    读取状态
     * @param sendUserIds   发送人结合
     * @param type          1为我收到的，0为我发送的
     * @return              分页对象
     */
    public PageWrapper<LogDetail> find(int pageNo, int pageSize, String orgId, String userId, String readStatus, String[] sendUserIds, String type) {
        PageWrapper<LogDetail> resultP = new PageWrapper<>();
        Page<LogDetail> page = new Page<LogDetail>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        Query query = findAll(orgId, userId, readStatus, sendUserIds, type);
        query.with(new Sort(Sort.Direction.DESC, "submitTime"));
        resultP = BeanUtils.mapPage(findPage(page, query), LogDetail.class);
        return resultP;
    }

    /**
     * 修改某条信息未读为已读
     * @param logId
     * @param userId
     * @return
     */
    public WriteResult updateReadByLogId(String logId, String userId) {
        Query query = findOne(logId, userId, LogConstant.BOTONG_ONE_STR);
        return  updateQuery(query, userId);
    }

    /**
     * 修改复合条件的所有未读为已读
     * @param orgId         企业ID
     * @param userId        memberId
     * @param sendUserIds   发送人
     * @return              无
     */
    public WriteResult updateReadAll(String orgId, String userId, String[] sendUserIds) {
        Query query = findAll(orgId, userId, "", sendUserIds, LogConstant.BOTONG_ONE_STR);
        return  updateQuery(query, userId);
    }

    /**
     * 组装更新的语句
     * @param query        查询对象
     * @param userId       用户id
     * @return              无
     */
    private WriteResult updateQuery(Query query, String userId) {
        /**
         * 1先删除未读，后增加已读
         */
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("$pull", new BasicDBObject("unreadUserId", userId));
        basicDBObject.put("$addToSet", new BasicDBObject("readUserId", userId));
        Update update = new BasicUpdate(basicDBObject);
        return  update(query, update);
    }

    /**
     * 根据logId组装查询对象
     * @param logId
     * @param userId
     * @return
     */
    private Query findOne(String logId, String userId, String isCompare) {
        Query query = new Query(Criteria.where("logId").is(logId));
        if (StringUtils.isNotEmpty(isCompare)) {
            query = new Query(Criteria.where("logId").is(logId).and("memberId").ne(userId));
        }
        return query;
    }

    /**
     * 根据日志查看
     * @param logId
     * @param userId
     * @return
     */
    public LogDetail findByLogId(String logId, String userId){
        return findOne(findOne(logId, userId,""));
    }

    /**
     * 根据条件组装查询对象
     * @param orgId         企业Id
     * @param userId        memberId
     * @param readStatus    未读0，已读1
     * @param sendUserIds   发送人
     * @param type          我收到的和我大宋的
     * @return              查询对象
     */
    private Query findAll(String orgId, String userId, String readStatus, String[] sendUserIds, String type) {
        Criteria criteria = Criteria.where("orgId").is(orgId);
        if (StringUtils.isNotBlank(readStatus)) {
            if(LogConstant.BOTONG_ZERO_STR.equals(readStatus) ) {
                criteria.and("readUserId").nin(userId).and("unreadUserId").is(userId);
            } else if (LogConstant.BOTONG_ONE_STR.equals(readStatus) ) {
                criteria.and("readUserId").is(userId);
            }
        }
        if (ArrayUtils.isNotEmpty(sendUserIds)) {
            criteria.and("memberId").in(sendUserIds);
        }
        //我收到的
        if (LogConstant.BOTONG_ONE_STR.equals(type)) {
            criteria.and("sendToUserId").is(userId);
        }
        criteria.and("deleteStatus").is(LogConstant.BOTONG_ZERO_NUM);
        Query query = new Query(criteria);
        return query;
    }

    /**
     * 删除日志
     * @param logId     日志Id
     * @param userId    用户Id
     * @return
     */
    public WriteResult delete(String logId, String userId) {
        Query query = findOne(logId, userId,"");
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("$set", new BasicDBObject("deleteStatus", LogConstant.BOTONG_ONE_NUM));
        Update update = new BasicUpdate(basicDBObject);
        return update(query, update);
    }

    /**
     * 批量删除日志
     * @param logIds    日志Ids
     * @return
     */
    public WriteResult deleteByIds(String[] logIds) {
        Query query = new Query(Criteria.where("logId").in(logIds));
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("$set", new BasicDBObject("deleteStatus", LogConstant.BOTONG_ONE_NUM));
        Update update = new BasicUpdate(basicDBObject);
        return update(query, update);
    }

    /**
     * web端查询日志列表
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
    public PageWrapper<LogDetail> findByConditionPage(int pageNo, int pageSize, String orgId, List<String> memberId, int submitType, String startDate, String endDate) {
        Page<LogDetail> page = new Page<>(pageNo, pageSize);
        Query query = makeCondition(orgId, memberId, submitType, startDate, endDate);
        return BeanUtils.mapPage(findPage(page, query), LogDetail.class);
    }

    /**
     * 根据条件查询所有的日志列表
     * @param orgId
     * @param memberId
     * @param submitType
     * @param startDate
     * @param endDate
     * @return
     */
    public List<LogDetail> findByConditionAll(String orgId, List<String> memberId, int submitType, String startDate, String endDate) {
        Query query = makeCondition(orgId, memberId, submitType, startDate, endDate);
        return find(query);
    }

    /**
     * 拼接sql-query
     * @param orgId
     * @param memberId
     * @param submitType
     * @param startDate
     * @param endDate
     * @return
     */
    private Query makeCondition(String orgId, List<String> memberId, int submitType, String startDate, String endDate) {
        Criteria criteria = new Criteria();
        if (memberId != null && !memberId.isEmpty()) {
            criteria.and("memberId").in(memberId);
        } else {
            Criteria.where("orgId").is(orgId);
        }

        if (submitType != 0) {
            criteria.and("submitType").is(submitType);
        }

        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            Date start = DateUtil.stringToDate(startDate);
            Date end = DateUtil.StringToDate(endDate + LogConstant.DAY_END_STR, DateStyle.YYYY_MM_DD_HH_MM_SS);
            criteria.andOperator(Criteria.where("submitTime").lte(end).gte(start));
        }
        criteria.and("deleteStatus").is(LogConstant.BOTONG_ZERO_NUM);
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "submitTime"));
        return query;
    }

    public WriteResult remove(String passportId) {
        Query query = new Query(Criteria.where("_id").is(passportId));
        return remove(query);
    }

    public WriteResult remove(String[] passportIds) {
        Query query = new Query(Criteria.where("_id").in(passportIds));
        return remove(query);
    }
}
