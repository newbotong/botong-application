package com.yunjing.botong.log.service.impl;

import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.params.LogParam;
import com.yunjing.botong.log.service.LogService;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.botong.log.vo.LogTemplateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * 日志服务实现
 *
 * @author 王开亮
 * @date 2018/4/9 9:53
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogTemplateService logTemplateService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Long createLog(LogParam logParam) {
        LogDetail entity = new LogDetail();
        LogTemplateVo vo = this.logTemplateService.queryLogTemplate(logParam.getTemplateId());
        entity.fromParam(logParam,vo);
        mongoTemplate.insert(entity);;
        return null;
    }
}
