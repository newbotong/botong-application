package com.yunjing.approval.processor.mq.configuration;

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
public class OrgAppMessageConfiguration extends DefaultQueueConfiguration {

    public final static String REMIND_QUEUE_NAME = "botong.approval-app.queue";

    @Override
    public String queueName() {
        return REMIND_QUEUE_NAME;
    }

    @Override
    public String exchange() {
        return "botong:org-app.exchange";
    }

    @Override
    public String routingKey() {
        return "botong:org-app.route";
    }
}
