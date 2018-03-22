package com.yunjing.info;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 资讯服务启动类
 * @author tandk
 * @date 2018/3/22 17:27
 */
@EnableEurekaClient
@SpringBootApplication
public class InfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfoApplication.class, args);
    }

}