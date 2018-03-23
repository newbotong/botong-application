package com.yunjing.sign.config;

import com.common.mybatis.config.BaseMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-plus配置文件
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/2/27 19:00
 * @description
 **/
@Configuration
@MapperScan("com.yunjing.sign.dao.mapper*")
public class MybatisPlusConfig extends BaseMybatisPlusConfig {

}
