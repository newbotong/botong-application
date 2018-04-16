package com.yunjing.info.processor.mq.configuration;

import com.yunjing.message.declare.configuration.DefaultQueueConfiguration;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 初始化企业配置
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@Component
public class TimingMessageConfiguration extends DefaultQueueConfiguration {

    @Override
    public String queueName() {
        return "botong-info.queue";
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
