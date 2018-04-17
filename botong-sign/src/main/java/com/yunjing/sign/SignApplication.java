package com.yunjing.sign;

import com.yunjing.mommon.annotation.EnableCommonLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;


/**
 * 签到服务
 *
 * @author jingwj
 * @date 2018/3/20 9:48
 */
@SpringBootApplication
@EnableEurekaClient
@Configuration
@EnableCommonLogger
public class SignApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignApplication.class, args);
	}
}
