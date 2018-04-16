package com.yunjing.approval.processor.mq.consumer;

import com.yunjing.approval.model.dto.OrgMemberMessage;
import com.yunjing.approval.processor.mq.configuration.OrgAppMessageConfiguration;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.IOrgModelService;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.message.share.org.OrgAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private IOrgModelService orgModelService;

    @Autowired
    private IApprovalUserService approvalUserService;

    public OrgAppMessageConsumer(OrgAppMessageConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void onMessageReceive(Message message) {
        Object messageObj = message.getObj();
        if (messageObj instanceof OrgAppMessage) {
            OrgAppMessage appMessage = (OrgAppMessage) messageObj;
            String companyId = appMessage.getCompanyId();
            if (StringUtils.isNotBlank(companyId) && appMessage.getMessageType().equals(OrgAppMessage.MessageType.INIT)) {
                orgModelService.createApprovalModel(companyId);
            } else if (StringUtils.isNotBlank(companyId) && appMessage.getMessageType().equals(OrgAppMessage.MessageType.DISBAND)) {
                orgModelService.deleteApprovalModel(companyId);
            }
        }else if(messageObj instanceof List){

            List list = (List) messageObj;
            List<OrgMemberMessage> insertList = new ArrayList<>();
            List<OrgMemberMessage> updateList = new ArrayList<>();
            List<OrgMemberMessage> deleteList = new ArrayList<>();
            list.forEach(obj -> {
                OrgMemberMessage memberMessage = (OrgMemberMessage) obj;
                if(memberMessage != null && memberMessage.getMessageType().equals(OrgMemberMessage.MessageType.INSERT)){
                    insertList.add(memberMessage);
                }else if (memberMessage != null && memberMessage.getMessageType().equals(OrgMemberMessage.MessageType.MODIFY)){
                    updateList.add(memberMessage);
                }else if(memberMessage != null && memberMessage.getMessageType().equals(OrgMemberMessage.MessageType.DELETE)){
                    deleteList.add(memberMessage);
                }
            });
            if(!insertList.isEmpty()){
                approvalUserService.addMember(insertList);
            }
            if(!insertList.isEmpty()){
                approvalUserService.updateMember(updateList);
            }
            if(!insertList.isEmpty()){
                approvalUserService.deleteMember(deleteList);
            }
        }

    }
}
