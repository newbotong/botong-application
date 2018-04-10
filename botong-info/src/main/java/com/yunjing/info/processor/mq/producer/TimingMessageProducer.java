package com.yunjing.info.processor.mq.producer;

import com.yunjing.info.processor.mq.configuration.TimingMessageConfiguration;
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
public class TimingMessageProducer extends AbstractMessageProducer<Message, TimingMessageConfiguration> {

    public TimingMessageProducer(TimingMessageConfiguration configuration) {
        super(configuration);
    }
}
