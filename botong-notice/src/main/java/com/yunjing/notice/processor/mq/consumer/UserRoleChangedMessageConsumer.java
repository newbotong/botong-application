package com.yunjing.notice.processor.mq.consumer;

import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.notice.processor.mq.configuration.UserRoleChangedMessageConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/15
 * @description
 **/
@Component
@MessageQueueDeclarable
public class UserRoleChangedMessageConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, UserRoleChangedMessageConfiguration> {


    @Autowired
    public UserRoleChangedMessageConsumer(UserRoleChangedMessageConfiguration configuration) {
        super(configuration);
    }

    @RabbitListener(queues = "#{userRoleChangedMessageConsumer.queueName()}")
    @Override
    public void onMessageReceive(@Payload Message message) {
        HashMap<Long, List<Long>> obj = (HashMap<Long, List<Long>>) message.getObj();
//        obj.forEach((memberId, roleIdList) -> {
//            redisRepository.delete(memberId);
//            redisRepository.put(memberId, roleIdList);
//        });

    }
}
