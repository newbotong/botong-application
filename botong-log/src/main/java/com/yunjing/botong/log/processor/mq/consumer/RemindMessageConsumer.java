package com.yunjing.botong.log.processor.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.processor.feign.handle.DangFeignClient;
import com.yunjing.botong.log.processor.feign.handle.OrgStructureFeignClient;
import com.yunjing.botong.log.processor.feign.handle.ThirdPartFeignClient;
import com.yunjing.botong.log.processor.mq.configuration.RemindMessageConfiguration;
import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 提醒消息接受者
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/27.
 */
@Slf4j
@Component
@MessageQueueDeclarable
public class RemindMessageConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, RemindMessageConfiguration> {

    /**
     * 组织结构rpc
     */
    @Autowired
    private OrgStructureFeignClient orgStructureFeignClient;

    /**
     * 第三方服务 rpc
     */
    @Autowired
    private ThirdPartFeignClient thirdPartFeignClient;

    /**
     * dang 服务rpc
     */
    @Autowired
    private DangFeignClient dangFeignClient;


    @Autowired
    private StringRedisTemplate redisTemplate;

    public RemindMessageConsumer(RemindMessageConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void onMessageReceive(@Payload Message message) {
        RemindVo remind = (RemindVo) redisTemplate.opsForHash().get(LogConstant.LOG_SET_REMIND, "6384939092378914816");
        log.info("remind:{},message:{}", JSON.toJSONString(remind), JSON.toJSONString(message));

        // 1. 校验当前member是否是管理员

        // 2. 如果不是管理员，根据remindType提醒

        // 3. 如果是管理员
        // 3.1 查询出当天所有已经发送日志的人员
        // 3.2 查询出所有管理的人员
        // 3.3 3.1与3.2结果取交集，剩下的则是没有发送日志的
        // 3.4 根据remindType处理3.3的结果
    }
}
