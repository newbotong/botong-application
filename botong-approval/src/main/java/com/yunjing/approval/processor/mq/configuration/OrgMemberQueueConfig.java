package com.yunjing.approval.processor.mq.configuration;

import com.yunjing.message.declare.configuration.DefaultQueueConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/4/4 9:17
 * @description:
 */
@Configuration
public class OrgMemberQueueConfig extends DefaultQueueConfiguration {

    @Override
    public String queueName() {
        return "botong.org-member-info.queue";
    }

    @Override
    public String exchange() {
        return "botong.org-member.exchange";
    }

    @Override
    public String routingKey() {
        return "botong.org-member.route";
    }


}
