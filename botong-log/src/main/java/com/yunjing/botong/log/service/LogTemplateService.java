package com.yunjing.botong.log.service;

import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;

/**
 * 日志模板服务
 *
 * @author 王开亮
 * @date 2018/4/2 10:48
 */
public interface LogTemplateService {

    /**
     * 创建日志模板
     * @param logTemplateParam  日志模板参数
     * @return
     */
    long createLogTemplate(LogTemplateParam logTemplateParam);
}
