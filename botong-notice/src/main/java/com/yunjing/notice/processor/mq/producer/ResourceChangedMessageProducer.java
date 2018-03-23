package com.yunjing.notice.processor.mq.producer;

import com.yunjing.message.declare.producer.AbstractMessageProducer;
import com.yunjing.message.model.Message;
import com.yunjing.notice.processor.mq.configuration.ResourceChangedMessageConfiguration;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/15
 * @description
 **/
@Component
public class ResourceChangedMessageProducer extends AbstractMessageProducer<Message, ResourceChangedMessageConfiguration> {
    public ResourceChangedMessageProducer(ResourceChangedMessageConfiguration configuration) {
        super(configuration);
    }
}
