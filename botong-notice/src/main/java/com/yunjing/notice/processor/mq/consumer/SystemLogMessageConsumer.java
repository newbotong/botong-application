package com.yunjing.notice.processor.mq.consumer;

import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.MessageConsumer;
import com.yunjing.message.declare.queue.AbstractMessageQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.notice.processor.mq.configuration.SystemLogQueueConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/7
 * @description
 **/
@Component
@MessageQueueDeclarable
public class SystemLogMessageConsumer extends AbstractMessageQueueDeclare<SystemLogQueueConfiguration> implements MessageConsumer<Message> {

    @Autowired
    public SystemLogMessageConsumer(SystemLogQueueConfiguration systemLogQueueConfiguration) {
        super(systemLogQueueConfiguration);
    }

    @RabbitListener(queues = "#{systemLogMessageConsumer.queueName()}")
    @Override
    public void onMessageReceive(Message payload) {

    }
}
