package com.yunjing.botong.log.dao;

import com.common.mongo.dao.Page;
import com.common.mongo.dao.impl.BaseMongoDaoImpl;
import com.common.mongo.util.BeanUtils;
import com.common.mongo.util.PageWrapper;
import com.mongodb.WriteResult;
import com.yunjing.botong.log.constant.LogConstant;
import com.yunjing.botong.log.entity.LogDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

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

    public PageWrapper<LogDetail> find(int pageNo, int pageSize, Long orgId, Long userId, String readStatus, Long[] sendUserIds) {
        PageWrapper<LogDetail> resultP = new PageWrapper();
        Page<LogDetail> page = new Page<LogDetail>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);
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

        criteria.and("sendToUserId").is(userId);
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "submitTime"));
        resultP = BeanUtils.mapPage(findPage(page, query), LogDetail.class);
        return resultP;
    }

    public LogDetail find(String passportId) {
        Query query = new Query(Criteria.where("_id").is(passportId));
        return findOne(query);
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
