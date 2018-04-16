package com.yunjing.botong.log.processor.mq.producer;

import com.yunjing.botong.log.processor.mq.configuration.RemindMessageConfiguration;
import com.yunjing.message.declare.producer.AbstractMessageProducer;
import com.yunjing.message.model.Message;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@Component
public class RemindMessageProducer extends AbstractMessageProducer<Message, RemindMessageConfiguration> {

    public RemindMessageProducer(RemindMessageConfiguration configuration) {
        super(configuration);
    }
}
