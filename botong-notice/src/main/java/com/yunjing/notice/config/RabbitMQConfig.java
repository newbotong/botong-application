package com.yunjing.notice.config;

import com.yunjing.message.config.GlobalMessageConfig;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/13
 * @description
 **/
@Configuration
public class RabbitMQConfig extends GlobalMessageConfig {

    public RabbitMQConfig(ApplicationContext context, ConnectionFactory connectionFactory) throws Exception {
        super(context,connectionFactory);
    }
}
