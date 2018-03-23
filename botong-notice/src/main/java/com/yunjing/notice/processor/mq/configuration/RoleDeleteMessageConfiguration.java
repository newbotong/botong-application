package com.yunjing.notice.processor.mq.configuration;

import com.yunjing.message.declare.configuration.DefaultQueueConfiguration;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/15
 * @description
 **/
@Component
public class RoleDeleteMessageConfiguration extends DefaultQueueConfiguration {
    @Override
    public String queueName() {
        return "botong.notice.queue";
    }

    @Override
    public String exchange() {
        return "botong.notice.topic";
    }

    @Override
    public String routingKey() {
        return "RoleDelete";
    }
}
