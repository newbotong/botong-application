package com.yunjing.approval.dao.cache;

import com.common.redis.BaseRedisService;
import com.yunjing.approval.excel.ApprovalExData;
import org.springframework.stereotype.Service;


/**
 * @author liuxiaopeng
 * @date 2018/03/05
 */
@Service
public class ApprovalRedisService extends BaseRedisService<ApprovalExData> {

    private static final String REDIS_KEY = "botong:approval:temp";

    @Override
    protected String getRedisKey() {
        return REDIS_KEY;
    }

}
