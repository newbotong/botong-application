package com.yunjing.botong.log.config;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 日志reids操作类
 *
 * @author tandk
 * @date 2018/4/9 17:58
 */
@Component
public class RedisLog extends AbstractRedisConfiguration {
    @Resource(name = "redisLogTemplate")
    private StringRedisTemplate temple;

    @Override
    public StringRedisTemplate getTemple() {
        return temple;
    }
}
