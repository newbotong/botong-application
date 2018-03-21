package com.yunjing.notice.config;

import com.common.mybatis.config.BaseMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * <p> mybatis 配置
 * </p>
 *
 * @author 李双喜
 * @since 2018/3/20.
 */
@Configuration
@MapperScan("com.yunjing.notice.mapper*")
public class MyBatisPlusConfig extends BaseMybatisPlusConfig {
}
