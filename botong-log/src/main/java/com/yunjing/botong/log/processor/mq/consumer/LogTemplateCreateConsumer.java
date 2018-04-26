package com.yunjing.botong.log.processor.mq.consumer;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.yunjing.botong.log.entity.LogTemplateEntity;
import com.yunjing.botong.log.entity.LogTemplateFieldEntity;
import com.yunjing.botong.log.mapper.LogTemplateFieldMapper;
import com.yunjing.botong.log.mapper.LogTemplateMapper;
import com.yunjing.botong.log.processor.mq.configuration.LogTemplateCreateQueueConfiguration;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.message.share.org.OrgAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.BuildInfoContributor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author auth
 * @date 2018/4/26 15:32
 */
@Slf4j
@Component
@MessageQueueDeclarable
public class LogTemplateCreateConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, LogTemplateCreateQueueConfiguration> {


    @Autowired
    private LogTemplateMapper logTemplateMapper;

    @Autowired
    private LogTemplateFieldMapper logTemplateFieldMapper;

    public LogTemplateCreateConsumer(LogTemplateCreateQueueConfiguration configuration) {
        super(configuration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessageReceive(Message message) {
        if (message.getObj() instanceof  OrgAppMessage){
            OrgAppMessage appMessage = (OrgAppMessage) message.getObj();
            // 查询系统默认模板
            LogTemplateEntity logTemplateEntity = new LogTemplateEntity();
            List<LogTemplateEntity> builtInLogTemplateList = logTemplateEntity.selectList(new EntityWrapper().eq("user_defined",0).eq("deleted",0));
            // 批量插入模板
            List<String> builtInLogTemplateIdList = new ArrayList<>();
            Map<String,String> builtInIdToNewId = new HashMap<>();
            for (int i = 0; i < builtInLogTemplateList.size(); i++) {
                LogTemplateEntity builtInLogTemplate = builtInLogTemplateList.get(i);
                String builtInId = builtInLogTemplate.getId();
                builtInLogTemplateIdList.add(builtInId);
                builtInLogTemplate.setId(IdWorker.get32UUID());
                builtInLogTemplate.setUserDefined(true);
                builtInLogTemplate.setOrgId(appMessage.getCompanyId());
                builtInLogTemplate.setCreateTime(System.currentTimeMillis());
                builtInLogTemplate.setUpdateTime(System.currentTimeMillis());
                builtInIdToNewId.put(builtInId,builtInLogTemplate.getId());
            }
            logTemplateMapper.batchCreateLogTemplateEntity(builtInLogTemplateList);

            // 查询日志项列表
            LogTemplateFieldEntity logTemplateFieldEntity = new LogTemplateFieldEntity();
            List<LogTemplateFieldEntity> builtInLogTemplateFieldList = logTemplateFieldEntity.selectList(new EntityWrapper().eq("deleted",0).in("template_id",builtInLogTemplateIdList));
            for (int i = 0; i < builtInLogTemplateFieldList.size(); i++) {
                LogTemplateFieldEntity templateFieldEntity = builtInLogTemplateFieldList.get(i);
                String builtInLogTemplateFieldId = templateFieldEntity.getId();
                templateFieldEntity.setId(IdWorker.get32UUID());
                templateFieldEntity.setTemplateId(builtInIdToNewId.get(templateFieldEntity.getTemplateId()));
                templateFieldEntity.setCurrently(true);
                templateFieldEntity.setVersion(1);
                templateFieldEntity.setCreateTime(System.currentTimeMillis());
                templateFieldEntity.setUpdateTime(System.currentTimeMillis());
            }
            logTemplateFieldMapper.batchInsertLogTemplateFields(builtInLogTemplateFieldList);

        }else{
            log.error("创建机构初始化日志模板收到非法消息 ： {}",message.getObj().getClass().getName());
        }
    }
}
