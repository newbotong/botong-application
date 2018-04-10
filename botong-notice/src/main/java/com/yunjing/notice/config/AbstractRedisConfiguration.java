package com.yunjing.notice.config;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis配置实例抽象类
 *
 * @author tandk
 * @date 2018/4/9 17:57
 */
public abstract class AbstractRedisConfiguration {
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
