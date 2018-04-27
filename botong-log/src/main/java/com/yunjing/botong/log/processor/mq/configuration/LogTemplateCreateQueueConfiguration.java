package com.yunjing.botong.log.processor.mq.configuration;

import com.yunjing.message.declare.configuration.DefaultQueueConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/4/4 9:17
 * @description:
 */
@Component
public class LogTemplateCreateQueueConfiguration extends DefaultQueueConfiguration {

    @Override
    public String queueName() {
        return "botong.log-app.queue";
    }

    @Override
    public String exchange() {
        return "botong.org-app.exchange";
    }

    @Override
    public String routingKey() {
        return "botong.org-app.route";
    }

}
