package com.yunjing.info.config;

import com.yunjing.message.config.GlobalMessageConfig;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置类
 *
 * @author 刘舒杰
 * @date 2018/3/20 16:34
 */
@Configuration
public class MessageConfig extends GlobalMessageConfig {
    public MessageConfig(ApplicationContext context, ConnectionFactory connectionFactory) throws Exception {
        super(context, connectionFactory);
    }
}
