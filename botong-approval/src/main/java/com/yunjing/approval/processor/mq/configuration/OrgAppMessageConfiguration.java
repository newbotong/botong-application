package com.yunjing.approval.processor.mq.configuration;

import com.yunjing.message.declare.configuration.DefaultQueueConfiguration;
import org.springframework.stereotype.Component;

/**
 * 企业信息消息配置
 *
 * @author 刘小鹏
 * @date 2018/04/22
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
        return "botong.org-app.exchange";
    }

    @Override
    public String routingKey() {
        return "botong.org-app.route";
    }
}
