package com.yunjing.notice.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author 谈东魁
 * @date 2018/4/9 17:19
 */
@Configuration
public class RedisReadonlyConfiguration {

    @Bean(name = "redisReadonlyTemplate")
    public StringRedisTemplate redisTemplate(@Value("${spring.redis-readonly.database}") int index,
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
        jedis.setPoolConfig(poolCofig(maxIdle, maxTotal, maxWaitMillis));
        // 初始化连接pool
        jedis.afterPropertiesSet();
        RedisConnectionFactory factory = jedis;

        return factory;
    }

    public JedisPoolConfig poolCofig(int maxIdle, int maxTotal, long maxWaitMillis) {
        JedisPoolConfig poolCofig = new JedisPoolConfig();
        poolCofig.setMaxIdle(maxIdle);
        poolCofig.setMaxTotal(maxTotal);
        poolCofig.setMaxWaitMillis(maxWaitMillis);
        return poolCofig;
    }

}
