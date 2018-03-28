package com.yunjing.botong.log.controller;

import com.yunjing.botong.log.processor.mq.producer.RemindMessageProducer;
import com.yunjing.message.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RemindMessageProducer testMessageProducer;

    @RequestMapping("/send")
    public String send() {
        Message message = Message.obtain("123123", System.currentTimeMillis(), "hello world!!!");
        testMessageProducer.sendMessage(message);
        return "success";
    }
}
