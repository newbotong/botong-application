package com.yunjing.info.config;

import com.common.mybatis.config.BaseMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * <p> mybatis-plus 分页拦截处理
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/23.
 */
@Configuration
@MapperScan("com.yunjing.info.mapper")
public class MyBatisPlusConfig extends BaseMybatisPlusConfig {
}
