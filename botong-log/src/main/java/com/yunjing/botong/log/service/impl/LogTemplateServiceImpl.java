package com.yunjing.botong.log.service.impl;

import com.yunjing.botong.log.entity.LogTemplateEntity;
import com.yunjing.botong.log.entity.LogTemplateEnumEntity;
import com.yunjing.botong.log.entity.LogTemplateEnumItemEntity;
import com.yunjing.botong.log.entity.LogTemplateFieldEntity;
import com.yunjing.botong.log.mapper.LogTemplateEnumItemMapper;
import com.yunjing.botong.log.mapper.LogTemplateEnumMapper;
import com.yunjing.botong.log.mapper.LogTemplateFieldMapper;
import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.botong.log.service.LogTemplateService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志模板服务实现类
 *
 * @author 王开亮
 * @date 2018/4/2 10:55
 */
@Service
public class LogTemplateServiceImpl implements LogTemplateService {

    @Autowired
    private LogTemplateFieldMapper logTemplateFieldMapper;

    @Autowired
    private LogTemplateEnumMapper logTemplateEnumMapper;

    @Autowired
    private LogTemplateEnumItemMapper logTemplateEnumItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createLogTemplate(LogTemplateParam logTemplateParam) {
        // 插入模板
        LogTemplateEntity entity = new LogTemplateEntity();
        entity.fromParam(logTemplateParam);
        entity.insert();

        // 批量插入字段
        List<LogTemplateFieldEntity> fields = logTemplateParam.getLogTemplateFieldEntity();

        List<LogTemplateEnumEntity> enums = new ArrayList<>();
        List<LogTemplateEnumItemEntity> enumItems = new ArrayList<>();

        for (int i = 0; i < fields.size() ; i++) {
            LogTemplateFieldEntity fieldEntity = fields.get(i);
            fieldEntity.setTemplateId(entity.getId());
            if(fieldEntity.getFieldType()==3){
                enums.add(fieldEntity.getLogTemplateEnumEntity());
                enumItems.addAll(fieldEntity.getLogTemplateEnumEntity().getLogTemplateEnumItemEntities());
            }
        }
        // 批量插入字段
        this.logTemplateFieldMapper.batchInsertLogTemplateFields(fields);

        // 批量插入枚举列表
        if (!CollectionUtils.isEmpty(enums)){
            logTemplateEnumMapper.batchInsertLogTemplateEnums(enums);
        }
        // 批量插入枚举项列表
        if (!CollectionUtils.isEmpty(enumItems)){
            logTemplateEnumItemMapper.batchInsertLogTemplateEnumItems(enumItems);
        }

        return entity.getId();
    }
}
