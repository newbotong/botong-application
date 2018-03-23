package com.yunjing.notice.processor.mq.producer;

import com.yunjing.message.declare.producer.AbstractMessageProducer;
import com.yunjing.message.model.Message;
import com.yunjing.notice.processor.mq.configuration.UserRoleChangedMessageConfiguration;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/15
 * @description
 **/
@Component
public class UserRoleChangedMessageProducer extends AbstractMessageProducer<Message, UserRoleChangedMessageConfiguration> {
    public UserRoleChangedMessageProducer(UserRoleChangedMessageConfiguration configuration) {
        super(configuration);
    }
}
