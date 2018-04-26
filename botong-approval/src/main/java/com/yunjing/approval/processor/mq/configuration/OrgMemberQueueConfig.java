package com.yunjing.approval.processor.mq.configuration;

import com.yunjing.message.declare.configuration.DefaultQueueConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * 企业成员信息消息配置
 *
 * @author 刘小鹏
 * @date 2018/04/22
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
