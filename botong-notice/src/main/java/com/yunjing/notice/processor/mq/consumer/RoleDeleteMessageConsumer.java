package com.yunjing.notice.processor.mq.consumer;

import com.yunjing.message.declare.consumer.MessageConsumer;
import com.yunjing.message.model.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/15
 * @description
 **/
@Component
public class RoleDeleteMessageConsumer implements MessageConsumer<Message> {

    @Resource
//    private SystemRoleResourceRedisRepository systemRoleResourceRedisRepository;

    @RabbitListener(queues = "botong.permission.queue")
    @Override
    public void onMessageReceive(Message message) {
//        List<Long> deleteRoleIds = (List<Long>) message.getObj();
//        deleteRoleIds.forEach(id -> systemRoleResourceRedisRepository.delete(id));
    }
}
