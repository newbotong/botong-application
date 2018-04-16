package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalUserMapper;
import com.yunjing.approval.model.dto.OrgMemberMessage;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.vo.Member;
import com.yunjing.approval.model.vo.MemberInfo;
import com.yunjing.approval.model.vo.OrgMemberVo;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.mommon.global.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 刘小鹏
 * @date 2018/03/26
 */
@Service
public class ApprovalUserServiceImpl extends BaseServiceImpl<ApprovalUserMapper, ApprovalUser> implements IApprovalUserService {

    @Override
    public boolean addMember(List<OrgMemberMessage> orgMemberMessages) {
        boolean isInserted = false;
        List<ApprovalUser> approvalUsers = new ArrayList<>();
        orgMemberMessages.forEach(orgMemberMessage -> {
            ApprovalUser approvalUser = new ApprovalUser();
            approvalUser.setId(orgMemberMessage.getMemberId());
            approvalUser.setName(orgMemberMessage.getMemberName());
            approvalUser.setPosition(orgMemberMessage.getPosition());
            approvalUser.setMobile(orgMemberMessage.getMobile());
            approvalUser.setAvatar(orgMemberMessage.getProfile());
            approvalUser.setOrgId(orgMemberMessage.getCompanyId());
            approvalUser.setColor(orgMemberMessage.getColor());
            List<String> deptIds = orgMemberMessage.getDeptIds();
            List<String> deptNames = orgMemberMessage.getDeptNames();
            String dIds = "";
            for (String deptId : deptIds) {
                dIds = deptId + ",";
            }
            approvalUser.setDeptId(dIds);
            String dNames = "";
            for (String deptName : deptNames) {
                dNames = deptName + ",";
            }
            approvalUser.setDeptId(dNames);
            approvalUsers.add(approvalUser);
        });

        if(!approvalUsers.isEmpty()){
            isInserted =  this.insertBatch(approvalUsers);
        }
        return isInserted;
    }

    @Override
    public boolean updateMember(List<OrgMemberMessage> orgMemberMessages) {
        boolean isUpdated = false;
        List<String> ids = orgMemberMessages.stream().map(OrgMemberMessage::getMemberId).collect(Collectors.toList());
        if(!ids.isEmpty()){
            List<ApprovalUser> list = this.selectList(Condition.create().in("id={0}", ids));
            if(!list.isEmpty()){
                isUpdated =  this.updateBatchById(list);
            }
        }
        return isUpdated;
    }

    @Override
    public boolean deleteMember(List<OrgMemberMessage> orgMemberMessages) {
        boolean isDeleted = false;
        List<String> ids = orgMemberMessages.stream().map(OrgMemberMessage::getMemberId).collect(Collectors.toList());
        if(!ids.isEmpty()){
            isDeleted = this.deleteBatchIds(ids);
        }
        return isDeleted;
    }
}
