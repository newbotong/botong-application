package com.yunjing.approval.config;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 公告reids操作类
 *
 * @author tandk
 * @date 2018/4/9 17:58
 */
@Component
public class RedisApproval extends AbstractRedisConfiguration {
    @Resource(name = "redisApprovalTemplate")
    private StringRedisTemplate temple;

    @Override
    public StringRedisTemplate getTemple() {
        return temple;
    }
}
