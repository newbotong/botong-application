package com.yunjing.notice.config;

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
public class RedisNotice extends AbstractRedisConfiguration {
    @Resource(name = "redisNoticeTemplate")
    private StringRedisTemplate temple;

    @Override
    public StringRedisTemplate getTemple() {
        return temple;
    }
}
