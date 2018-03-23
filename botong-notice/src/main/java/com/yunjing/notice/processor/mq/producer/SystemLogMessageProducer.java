package com.yunjing.notice.processor.mq.producer;

import com.yunjing.message.declare.producer.AbstractMessageProducer;
import com.yunjing.message.model.Message;
import com.yunjing.notice.processor.mq.configuration.SystemLogQueueConfiguration;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/7
 * @description
 **/
@Component
public class SystemLogMessageProducer extends AbstractMessageProducer<Message, SystemLogQueueConfiguration> {

    public SystemLogMessageProducer(SystemLogQueueConfiguration configuration) {
        super(configuration);
    }
}
