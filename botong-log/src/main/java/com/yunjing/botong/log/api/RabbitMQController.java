package com.yunjing.botong.log.api;

import com.yunjing.botong.log.processor.mq.producer.RemindMessageProducer;
import com.yunjing.message.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * <p> rabbitmq 测试
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@RestController
@RequestMapping("/rabbitmq")
public class RabbitMQController {

    @Autowired
    private RemindMessageProducer remindMessageProducer;

    @RequestMapping("/send")
    public String send() {
        Message message = Message.obtain("123123", System.currentTimeMillis(), "hello world!!!");
        remindMessageProducer.sendMessage(message);
        return "success";
    }
}
