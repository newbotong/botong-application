package com.yunjing.approval.processor.mq.consumer;

import com.yunjing.approval.config.RedisApproval;
import com.yunjing.approval.processor.mq.configuration.OrgAppMessageConfiguration;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.IOrgModelService;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.message.share.org.OrgAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 提醒消息接受者
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/27.
 */
@Slf4j
@Component
@MessageQueueDeclarable
public class OrgAppMessageConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, OrgAppMessageConfiguration> {

    /**
     * 应用中心
     */
    @Autowired
    private AppCenterService appCenterService;
    @Autowired
    private IOrgModelService orgModelService;


    public OrgAppMessageConsumer(OrgAppMessageConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void onMessageReceive(Message message) {
        Object messageObj = message.getObj();
        if(messageObj instanceof OrgAppMessage){
            OrgAppMessage appMessage = (OrgAppMessage) messageObj;
            String companyId = appMessage.getCompanyId();
            if(StringUtils.isNotBlank(companyId)){
                if(appMessage.getMessageType().equals(OrgAppMessage.MessageType.INIT)){
                    orgModelService.createApprovalModel(companyId);
                }else if(appMessage.getMessageType().equals(OrgAppMessage.MessageType.DISBAND)){
                    try {
                        orgModelService.deleteApprovalModel(companyId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
