package com.yunjing.notice.config;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 谈东魁
 * @date 2018/4/9 17:58
 */
@Component
public class RedisReadonly extends AbRedisConfiguration {
    @Resource(name = "redisReadonlyTemplate")
    private StringRedisTemplate temple;

    @Override
    public StringRedisTemplate getTemple() {
        return temple;
    }
}
