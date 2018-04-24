package com.yunjing.approval.processor.mq.consumer;

import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.processor.mq.configuration.OrgMemberQueueConfig;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.IOrgModelService;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.message.share.org.OrgMemberMessage;
import lombok.extern.slf4j.Slf4j;
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
public class OrgMemberMessageConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, OrgMemberQueueConfig> {

    @Autowired
    private IOrgModelService orgModelService;

    @Autowired
    private IApprovalUserService approvalUserService;

    public OrgMemberMessageConsumer(OrgMemberQueueConfig configuration) {
        super(configuration);
    }

    @Override
    public void onMessageReceive(Message message) {
        Object messageObj = message.getObj();
        if (messageObj instanceof List) {
            List list = (List) messageObj;
            List<ApprovalUser> insertList = new ArrayList<>();
            List<ApprovalUser> updateList = new ArrayList<>();
            List<ApprovalUser> deleteList = new ArrayList<>();
            for (Object obj : list) {
                OrgMemberMessage memberMessage = (OrgMemberMessage) obj;
                if (memberMessage != null && memberMessage.getMessageType().equals(OrgMemberMessage.MessageType.INSERT)) {
                    ApprovalUser approvalUser = covertObj(memberMessage);
                    insertList.add(approvalUser);
                } else if (memberMessage != null && memberMessage.getMessageType().equals(OrgMemberMessage.MessageType.MODIFY)) {
                    ApprovalUser approvalUser = covertObj(memberMessage);
                    updateList.add(approvalUser);
                } else if (memberMessage != null && memberMessage.getMessageType().equals(OrgMemberMessage.MessageType.DELETE)) {
                    ApprovalUser approvalUser = covertObj(memberMessage);
                    deleteList.add(approvalUser);
                }
            }
            if (!insertList.isEmpty()) {
                approvalUserService.addMember(insertList);
            }
            if (!updateList.isEmpty()) {
                approvalUserService.updateMember(updateList);
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
        String dIds = "";
        if (deptIds != null && !deptIds.isEmpty()) {
            for (String deptId : deptIds) {
                dIds = deptId + ",";
            }
        }
        approvalUser.setDeptId(dIds);
        List<String> deptNames = memberMessage.getDeptNames();
        String dNames = "";
        if (deptNames != null && !deptNames.isEmpty()) {
            for (String deptName : deptNames) {
                dNames = deptName + ",";
            }
        }
        approvalUser.setDeptName(dNames);

        return approvalUser;
    }
}
