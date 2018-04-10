package com.yunjing.botong.log.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis数据源配置
 *
 * @author tandk
 * @date 2018/4/9 17:19
 */
@Configuration
public class RedisConfiguration {

    @Bean(name = "redisReadonlyTemplate")
    public StringRedisTemplate redisReadonlyTemplate(@Value("${spring.redis-readonly.database}") int index,
                                                     @Value("${spring.redis-readonly.host}") String hostName,
                                                     @Value("${spring.redis-readonly.port}") int port,
                                                     @Value("${spring.redis-readonly.password}") String password,
                                                     @Value("${spring.redis-readonly.pool.max-idle}") int maxIdle,
                                                     @Value("${spring.redis-readonly.pool.max-active}") int maxTotal,
                                                     @Value("${spring.redis-readonly.pool.max-wait}") long maxWaitMillis) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(
                connectionFactory(hostName, port, password, maxIdle, maxTotal, index, maxWaitMillis));

        return stringRedisTemplate;
    }

    @Primary
    @Bean(name = "redisLogTemplate")
    public StringRedisTemplate redisLogTemplate(@Value("${spring.redis-log.database}") int index,
                                                @Value("${spring.redis-log.host}") String hostName,
                                                @Value("${spring.redis-log.port}") int port,
                                                @Value("${spring.redis-log.password}") String password,
                                                @Value("${spring.redis-log.pool.max-idle}") int maxIdle,
                                                @Value("${spring.redis-log.pool.max-active}") int maxTotal,
                                                @Value("${spring.redis-log.pool.max-wait}") long maxWaitMillis) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(
                connectionFactory(hostName, port, password, maxIdle, maxTotal, index, maxWaitMillis));

        return stringRedisTemplate;
    }

    public RedisConnectionFactory connectionFactory(String hostName, int port, String password, int maxIdle,
                                                    int maxTotal, int index, long maxWaitMillis) {
        JedisConnectionFactory jedis = new JedisConnectionFactory();
        jedis.setHostName(hostName);
        jedis.setPort(port);
        if (StringUtils.isNotEmpty(password)) {
            jedis.setPassword(password);
        }
        if (index != 0) {
            jedis.setDatabase(index);
        }
        jedis.setPoolConfig(poolConfig(maxIdle, maxTotal, maxWaitMillis));
        // 初始化连接pool
        jedis.afterPropertiesSet();

        return jedis;
    }

    public JedisPoolConfig poolConfig(int maxIdle, int maxTotal, long maxWaitMillis) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        return poolConfig;
    }
}
