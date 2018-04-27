package com.yunjing.botong.log.processor.mq.producer;

import com.yunjing.botong.log.processor.mq.configuration.LogTemplateCreateQueueConfiguration;
import com.yunjing.message.declare.producer.AbstractMessageProducer;
import com.yunjing.message.model.Message;
import org.springframework.stereotype.Component;

/**
 * @author auth
 * @date 2018/4/26 16:28
 */
@Component
public class LogTemplateCreateProducer extends AbstractMessageProducer<Message, LogTemplateCreateQueueConfiguration>{

    public LogTemplateCreateProducer(LogTemplateCreateQueueConfiguration configuration) {
        super(configuration);
    }


}
