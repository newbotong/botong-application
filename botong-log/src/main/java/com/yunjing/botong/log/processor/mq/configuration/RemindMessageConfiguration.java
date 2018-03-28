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

    @Override
    public String queueName() {
        return "botong.log-remind.queue";
    }

    @Override
    public String exchange() {
        return "botong.log-remind.exchange";
    }

    @Override
    public String routingKey() {
        return "botong.log-remind.routing";
    }
}
