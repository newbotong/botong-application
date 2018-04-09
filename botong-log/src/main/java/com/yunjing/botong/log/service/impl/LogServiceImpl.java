package com.yunjing.botong.log.service.impl;

import com.yunjing.botong.log.entity.LogEntity;
import com.yunjing.botong.log.params.LogParam;
import com.yunjing.botong.log.service.LogService;
import org.springframework.stereotype.Service;

/**
 * 日志服务实现
 *
 * @author 王开亮
 * @date 2018/4/9 9:53
 */
@Service
public class LogServiceImpl implements LogService {


    @Override
    public Long createLog(LogParam logParam) {
        LogEntity entity = new LogEntity();
        entity.fromParam(logParam);
        return null;
    }
}
