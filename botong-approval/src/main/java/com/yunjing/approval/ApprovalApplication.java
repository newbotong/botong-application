package com.yunjing.approval;

import com.yunjing.mommon.annotation.EnableCommonLogger;
import com.yunjing.mommon.base.BaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * 审批服务--启动类
 *
 * @author 刘小鹏
 * @date 2018/03/20
 */
@Configuration
@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients
@EnableCommonLogger
public class ApprovalApplication extends BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApprovalApplication.class, args);
    }
}
