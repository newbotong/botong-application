package com.yunjing.botong.log.config;

import com.yunjing.message.config.GlobalMessageConfig;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * <p> rabbitmq 配置
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/27.
 */
@Configuration
public class RabbitMQConfig extends GlobalMessageConfig {

    public RabbitMQConfig(ApplicationContext context, ConnectionFactory factory) throws Exception {
        super(context, factory);
    }
}
