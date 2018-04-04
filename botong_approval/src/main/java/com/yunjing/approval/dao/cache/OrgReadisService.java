package com.yunjing.approval.dao.cache;

import com.common.redis.BaseRedisService;
import com.yunjing.approval.model.entity.Org;
import org.springframework.stereotype.Service;

/**
 * @author 刘小鹏
 */
@Service
public class OrgReadisService extends BaseRedisService<Org> {
    private static final String REDIS_KEY = "ORG";

    @Override
    protected String getRedisKey() {
        return REDIS_KEY;
    }
}
