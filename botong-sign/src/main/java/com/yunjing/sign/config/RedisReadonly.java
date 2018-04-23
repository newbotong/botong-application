package com.yunjing.sign.config;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 只读reids操作类
 *
 * @author tandk
 * @date 2018/4/9 17:58
 */
@Component
public class RedisReadonly extends AbstractRedisConfiguration {
    @Resource(name = "redisReadonlyTemplate")
    private StringRedisTemplate temple;

    @Override
    public StringRedisTemplate getTemple() {
        return temple;
    }
}
