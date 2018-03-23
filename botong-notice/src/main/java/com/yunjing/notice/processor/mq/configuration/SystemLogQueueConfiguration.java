package com.yunjing.notice.processor.mq.configuration;

import com.yunjing.message.constants.ExchangeType;
import com.yunjing.message.declare.configuration.DefaultQueueConfiguration;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/13
 * @description
 **/
@Component
public class SystemLogQueueConfiguration extends DefaultQueueConfiguration {
    @Override
    public String queueName() {
        return "botong.admin.system.log";
    }

    @Override
    public String exchange() {
        return "botong.admin.system.log";
    }

    @Override
    public String routingKey() {
        return null;
    }

    @Override
    public ExchangeType exchangeType() {
        return ExchangeType.FANOUT;
    }
}
