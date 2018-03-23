package com.yunjing.notice.processor.mq.consumer;

import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.notice.processor.mq.configuration.ResourceChangedMessageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/7
 * @description
 **/
@Component
@MessageQueueDeclarable
public class ResourceChangedMessageConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, ResourceChangedMessageConfiguration> {

    @Autowired
    public ResourceChangedMessageConsumer(ResourceChangedMessageConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void onMessageReceive(@Payload Message payload) {

    }
}
