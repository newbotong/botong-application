package com.yunjing.notice.config;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author 谈东魁
 * @date 2018/4/9 17:57
 */
public abstract class AbRedisConfiguration {
    protected StringRedisTemplate temple;

    public void setData(String key, String value) {
        getTemple().opsForValue().set(key, value);
    }

    public String getData(String key) {
        return getTemple().opsForValue().get(key);
    }

    public StringRedisTemplate getTemple() {
        return temple;
    }
}
