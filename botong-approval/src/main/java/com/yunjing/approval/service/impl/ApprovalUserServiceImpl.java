package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.common.redis.share.UserInfo;
import com.yunjing.approval.common.ContactsCommon;
import com.yunjing.approval.dao.mapper.ApprovalUserMapper;
import com.yunjing.approval.model.dto.CompanyDeptDTO;
import com.yunjing.approval.model.dto.MemberDTO;
import com.yunjing.approval.model.dto.MemberListDTO;
import com.yunjing.approval.model.dto.OrgMemberMessage;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.mommon.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 刘小鹏
 * @date 2018/03/26
 */
@Service
public class ApprovalUserServiceImpl extends BaseServiceImpl<ApprovalUserMapper, ApprovalUser> implements IApprovalUserService {

    @Autowired
    StringRedisTemplate redisTemplate;

    private Type companyDeptType = new TypeReference<List<CompanyDeptDTO>>() {
    }.getType();

    private Type memberType = new TypeReference<MemberDTO>() {
    }.getType();


    private Type userInfoType = new TypeReference<UserInfo>() {
    }.getType();
    @Override
    public boolean addMember(List<OrgMemberMessage> orgMemberMessages) {
        boolean isInserted = false;
        List<ApprovalUser> approvalUsers = new ArrayList<>();
        List<ApprovalUser> list = this.selectList(Condition.create());
        List<String> ids = list.stream().map(ApprovalUser::getId).collect(Collectors.toList());
        orgMemberMessages.forEach(orgMemberMessage -> {
            Set<String> idSet = ids.stream().filter(id -> id.equals(orgMemberMessage.getMemberId())).collect(Collectors.toSet());
            if (idSet.size() == 0) {
                ApprovalUser approvalUser = new ApprovalUser();
                approvalUser.setId(orgMemberMessage.getMemberId());
                approvalUser.setName(orgMemberMessage.getMemberName());
                approvalUser.setPosition(orgMemberMessage.getPosition());
                approvalUser.setMobile(orgMemberMessage.getMobile());
                approvalUser.setAvatar(orgMemberMessage.getProfile());
                approvalUser.setOrgId(orgMemberMessage.getCompanyId());
                approvalUser.setPassportId(orgMemberMessage.getPassportId());
                approvalUser.setColor(orgMemberMessage.getColor());
                List<String> deptIds = orgMemberMessage.getDeptIds();
                List<String> deptNames = orgMemberMessage.getDeptNames();
                String dIds = "";
                if (deptIds != null && !deptIds.isEmpty()) {
                    for (String deptId : deptIds) {
                        dIds = deptId + ",";
                    }
                }
                approvalUser.setDeptId(dIds);
                String dNames = "";
                if (deptNames != null && !deptNames.isEmpty()) {
                    for (String deptName : deptNames) {
                        dNames = deptName + ",";
                    }
                }
                approvalUser.setDeptName(dNames);
                approvalUsers.add(approvalUser);
            }
        });

        if (!approvalUsers.isEmpty()) {
            isInserted = this.insertBatch(approvalUsers);
        }
        return isInserted;
    }

    @Override
    public boolean updateMember(List<OrgMemberMessage> orgMemberMessages) {
        boolean isUpdated = false;
        List<String> ids = orgMemberMessages.stream().map(OrgMemberMessage::getMemberId).collect(Collectors.toList());
        if (!ids.isEmpty()) {
            List<ApprovalUser> list = this.selectList(Condition.create().in("id={0}", ids));
            if (!list.isEmpty()) {
                isUpdated = this.updateBatchById(list);
            }
        }
        return isUpdated;
    }

    @Override
    public boolean deleteMember(List<OrgMemberMessage> orgMemberMessages) {
        boolean isDeleted = false;
        List<String> ids = orgMemberMessages.stream().map(OrgMemberMessage::getMemberId).collect(Collectors.toList());
        if (!ids.isEmpty()) {
            isDeleted = this.deleteBatchIds(ids);
        }
        return isDeleted;
    }

    @Override
    public boolean updateContract(String companyId ,String userId,Integer choiceContacts) {
        List<MemberListDTO> memberListDTOList = new ArrayList<>();
        List<CompanyDeptDTO> companyDeptDTOList = JSON.parseObject(redisTemplate.opsForList().range(ContactsCommon.ORG_COMPANY_DEPT_KEY + companyId, 0, -1).toString(), companyDeptType);
        for (CompanyDeptDTO companyDeptDTO : companyDeptDTOList) {
//              获取根部门下的成员列表
            if (redisTemplate.hasKey(ContactsCommon.ORG_DEPT_MEMBER_KEY + companyDeptDTO.getId())) {
                    List<String> memberIdList = redisTemplate.opsForList().range(ContactsCommon.ORG_DEPT_MEMBER_KEY + companyDeptDTO.getId(), 0, -1);
                    for (String memberId : memberIdList) {
                        MemberListDTO memberListDTO = getMemberListDTO(memberId);
                        if (memberListDTO != null) {
//                          校验是否为选择联系人列表(0:否 1：是)
                            if (choiceContacts == Integer.parseInt(ContactsCommon.COMMON_1)) {
                                if (!memberListDTO.getPassportId().equals(userId)) {
                                    memberListDTOList.add(memberListDTO);
                                }
                            } else {
                                memberListDTOList.add(memberListDTO);
                            }
                        }
                    }
                }
        }
        List<OrgMemberMessage> list = new ArrayList<>();
        for (MemberListDTO member : memberListDTOList) {
            OrgMemberMessage orgMemeber = new OrgMemberMessage();
            orgMemeber.setMemberId(member.getMemberId());
            orgMemeber.setCompanyId(companyId);
//            orgMemeber.setDeptIds(new ArrayList<>(member.g));
//            orgMemeber.setDeptNames(member.getDeptNames());
            orgMemeber.setMemberName(member.getName());
            orgMemeber.setMobile(member.getMobile());
            orgMemeber.setPosition(member.getPosition());
            orgMemeber.setProfile(member.getProfile());
            orgMemeber.setPassportId(member.getPassportId());
            list.add(orgMemeber);
        }
        boolean b = this.addMember(list);
        return b;
    }

    /**
     * 获取成员信息
     *
     * @param memberId      成员id
     * @return
     */
    private MemberListDTO getMemberListDTO(String memberId) {
        MemberListDTO memberListDTO = null;
        if (redisTemplate.opsForHash().hasKey(ContactsCommon.ORG_MEMBER_KEY, memberId)) {
            memberListDTO = new MemberListDTO();
            MemberDTO memberDTO = JSON.parseObject(redisTemplate.opsForHash().get(ContactsCommon.ORG_MEMBER_KEY, memberId).toString(), memberType);
            BeanUtils.copy(memberDTO, memberListDTO);
//          获取用户昵称、头像
            if (redisTemplate.opsForHash().hasKey(ContactsCommon.ORG_USER_KEY, memberDTO.getPassportId())) {
                UserInfo userInfo = JSON.parseObject(redisTemplate.opsForHash().get(ContactsCommon.ORG_USER_KEY, memberDTO.getPassportId()).toString(), userInfoType);
                memberListDTO.setName(userInfo.getNick());
                memberListDTO.setProfile(userInfo.getProfile());
            }
            memberListDTO.setMemberId(memberDTO.getId());
        }
        return memberListDTO;
    }
}
