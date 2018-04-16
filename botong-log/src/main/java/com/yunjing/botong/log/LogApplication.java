package com.yunjing.botong.log;

import com.yunjing.mommon.base.BaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 日报服务启动类
 *
 * @version 1.0.0
 * @author: 王开亮
 * @date 2018/3/23 16:57
 * @description
 **/
@EnableEurekaClient
@SpringBootApplication
public class LogApplication extends BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
    }
}
