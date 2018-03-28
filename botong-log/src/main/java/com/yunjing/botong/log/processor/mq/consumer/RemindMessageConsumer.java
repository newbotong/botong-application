package com.yunjing.botong.log.processor.mq.consumer;

import com.google.gson.Gson;
import com.yunjing.botong.log.processor.mq.configuration.RemindMessageConfiguration;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 提醒消息接受者
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/27.
 */
@Slf4j
@Component
@MessageQueueDeclarable
public class RemindMessageConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, RemindMessageConfiguration> {

    public RemindMessageConsumer(RemindMessageConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void onMessageReceive(@Payload Message message) {

        log.info("{}", new Gson().toJson(message));

    }
}
