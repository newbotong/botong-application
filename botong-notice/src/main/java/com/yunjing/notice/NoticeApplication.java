package com.yunjing.notice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * <p> 公告服务启动类
 * </p>
 *
 * @author 李双喜
 * @since 2018/3/20.
 */
@EnableEurekaClient
@Configuration
@SpringBootApplication
@EnableFeignClients

public class NoticeApplication {

    public static void main(String... args) {
        SpringApplication.run(NoticeApplication.class, args);
    }
}
