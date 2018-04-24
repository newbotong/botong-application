package com.yunjing.botong.log.config;

import com.common.mongo.config.BaseMongoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/3/5 20:31
 * @description:
 */
@Configuration
public class MongoConfig extends BaseMongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getUri() {
        return mongoUri;
    }

    /*@Bean
    public MongoClientURI getMongoClientURI() throws Exception {
        return new MongoClientURI(URI);
    }

    @Bean
    public MongoDbFactory getMongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(getMongoClientURI());
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(getMongoDbFactory());//还有其它的初始化方法。
    }*/
}