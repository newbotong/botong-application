package com.yunjing.approval.processor.mq.consumer;

import com.yunjing.approval.processor.mq.configuration.OrgAppMessageConfiguration;
import com.yunjing.approval.service.IOrgModelService;
import com.yunjing.message.annotation.MessageConsumerDeclarable;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumer;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.message.share.org.OrgAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 企业信息数据消息接受者
 *
 * @author 刘小鹏
 * @date 2018/04/22
 */
@Slf4j
@Component
@MessageConsumerDeclarable
public class OrgAppMessageConsumer extends AbstractMessageConsumer<Message> {

    @Autowired
    private IOrgModelService orgModelService;

    private OrgAppMessageConfiguration configuration;

    public OrgAppMessageConsumer(OrgAppMessageConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void onMessageReceive(Message message) {
        Object messageObj = message.getObj();
        if (messageObj instanceof OrgAppMessage) {
            OrgAppMessage appMessage = (OrgAppMessage) messageObj;
            String companyId = appMessage.getCompanyId();
            if(appMessage.getMessageType() == null){
                log.warn("消息接受缺少消息类型，MessageType " + appMessage.getMessageType());
                return;
            }
            if (StringUtils.isNotBlank(companyId) && appMessage.getMessageType().equals(OrgAppMessage.MessageType.INIT)) {
                orgModelService.createApprovalModel(companyId);
            } else if (StringUtils.isNotBlank(companyId) && appMessage.getMessageType().equals(OrgAppMessage.MessageType.DISBAND)) {
                orgModelService.deleteApprovalModel(companyId);
            }
        }
    }

    @Override
    public String queueName() {
        return configuration.queueName();
    }
}
