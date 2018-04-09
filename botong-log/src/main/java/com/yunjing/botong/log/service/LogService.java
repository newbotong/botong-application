package com.yunjing.botong.log.service;

import com.yunjing.botong.log.params.LogParam;

/**
 * 日志服务
 *
 * @author 王开亮
 * @date 2018/4/9 9:52
 */
public interface LogService {

    Long createLog(LogParam logParam);
}
