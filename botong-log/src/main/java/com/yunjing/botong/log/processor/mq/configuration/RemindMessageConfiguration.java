package com.yunjing.botong.log.processor.mq.configuration;

import com.yunjing.message.declare.configuration.DefaultQueueConfiguration;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 提醒消息配置
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@Component
public class RemindMessageConfiguration extends DefaultQueueConfiguration {

    // TODO 修改
    public final static String REMIND_QUEUE_NAME = "botong.log-remind.queue-test";

    @Override
    public String queueName() {
        return REMIND_QUEUE_NAME;
    }

    @Override
    public String exchange() {
        return "botong.remind.exchange";
    }

    @Override
    public String routingKey() {
        return REMIND_QUEUE_NAME;
    }
}
