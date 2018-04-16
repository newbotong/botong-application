package com.yunjing.botong.log.config;

import com.common.mybatis.config.BaseMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author auth
 * @date 2018/3/26 10:28
 */
@Configuration
@MapperScan("com.yunjing.botong.log.mapper")
public class MybatisPlusConfig extends BaseMybatisPlusConfig {
}
