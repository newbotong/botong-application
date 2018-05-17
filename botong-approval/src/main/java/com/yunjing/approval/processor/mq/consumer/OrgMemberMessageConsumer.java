package com.yunjing.approval.processor.mq.consumer;

import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.processor.mq.configuration.OrgMemberQueueConfig;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.message.annotation.MessageConsumerDeclarable;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumer;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.message.share.org.OrgMemberMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 企业成员数据信息消息接受者
 *
 * @author 刘小鹏
 * @date 2018/04/22
 */
@Slf4j
@Component
@MessageConsumerDeclarable
public class OrgMemberMessageConsumer extends AbstractMessageConsumer<Message> {

    @Autowired
    private IApprovalUserService approvalUserService;

    @Autowired
    private OrgMemberQueueConfig orgMemberQueueConfig;

    @Override
    public void onMessageReceive(Message message) {
        Object messageObj = message.getObj();
        if (messageObj instanceof List) {
            List list = (List) messageObj;
            List<ApprovalUser> insertList = new ArrayList<>();
            List<ApprovalUser> deleteList = new ArrayList<>();
            for (Object obj : list) {
                OrgMemberMessage memberMessage = (OrgMemberMessage) obj;
                if (memberMessage != null && memberMessage.getMessageType().equals(OrgMemberMessage.MessageType.INSERT)) {
                    ApprovalUser approvalUser = covertObj(memberMessage);
                    insertList.add(approvalUser);
                } else if (memberMessage != null && memberMessage.getMessageType().equals(OrgMemberMessage.MessageType.DELETE)) {
                    ApprovalUser approvalUser = covertObj(memberMessage);
                    deleteList.add(approvalUser);
                }
            }
            if (!insertList.isEmpty()) {
                approvalUserService.addMember(insertList);
            }
            if (!deleteList.isEmpty()) {
                approvalUserService.deleteMember(deleteList);
            }
        }
    }

    private ApprovalUser covertObj(OrgMemberMessage memberMessage) {
        ApprovalUser approvalUser = new ApprovalUser();
        approvalUser.setPassportId(memberMessage.getPassportId());
        approvalUser.setColor(memberMessage.getColor());
        approvalUser.setOrgId(memberMessage.getCompanyId());
        approvalUser.setAvatar(memberMessage.getProfile());
        approvalUser.setName(memberMessage.getMemberName());
        approvalUser.setMobile(memberMessage.getMobile());
        approvalUser.setPosition(memberMessage.getPosition());
        approvalUser.setId(memberMessage.getMemberId());
        List<String> deptIds = memberMessage.getDeptIds();
        String dIds = Optional.ofNullable(deptIds).orElseGet(ArrayList::new).stream().collect(Collectors.joining(","));
        approvalUser.setDeptId(dIds);
        List<String> deptNames = memberMessage.getDeptNames();
        String dNames = Optional.ofNullable(deptNames).orElseGet(ArrayList::new).stream().collect(Collectors.joining(","));
        approvalUser.setDeptName(dNames);
        return approvalUser;
    }

    @Override
    public String queueName() {
        return orgMemberQueueConfig.queueName();
    }
}
