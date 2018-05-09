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
    public boolean addMember(List<ApprovalUser> approvalUserList) {
        boolean isInserted = false;
        List<ApprovalUser> approvalUsers = new ArrayList<>();
        List<ApprovalUser> list = this.selectList(Condition.create());
        List<String> memberIds = list.stream().map(ApprovalUser::getId).collect(Collectors.toList());
        approvalUserList.forEach(approvalUser -> {
            Set<String> memberIdSet = memberIds.stream().filter(id -> id.equals(approvalUser.getId())).collect(Collectors.toSet());
            // 如果成员信息不存在就插入
            if (memberIdSet.size() == 0) {
                ApprovalUser newUser = new ApprovalUser();
                newUser.setId(approvalUser.getId());
                newUser.setName(approvalUser.getName());
                newUser.setPosition(approvalUser.getPosition());
                newUser.setMobile(approvalUser.getMobile());
                newUser.setAvatar(approvalUser.getAvatar());
                newUser.setOrgId(approvalUser.getOrgId());
                newUser.setPassportId(approvalUser.getPassportId());
                newUser.setColor(approvalUser.getColor());
                newUser.setDeptId(approvalUser.getDeptId());
                newUser.setDeptName(approvalUser.getDeptName());
                approvalUsers.add(approvalUser);
            } else {
                // 如果成员信息已存在就更新
                memberIdSet.forEach(memberId -> {
                    ApprovalUser approvalUser1 = this.selectById(memberId);
                    approvalUser1.setName(approvalUser.getName());
                    approvalUser1.setPosition(approvalUser.getPosition());
                    approvalUser1.setMobile(approvalUser.getMobile());
                    approvalUser1.setAvatar(approvalUser.getAvatar());
                    approvalUser1.setOrgId(approvalUser.getOrgId());
                    approvalUser1.setPassportId(approvalUser.getPassportId());
                    approvalUser1.setColor(approvalUser.getColor());
                    approvalUser1.setDeptId(approvalUser.getDeptId());
                    approvalUser1.setDeptName(approvalUser.getDeptName());
                    approvalUsers.add(approvalUser1);
                });
            }
        });
        if (!approvalUsers.isEmpty()) {
            isInserted = this.insertOrUpdateBatch(approvalUsers);
        }
        return isInserted;
    }

    @Override
    public boolean deleteMember(List<ApprovalUser> approvalUserList) {
        boolean isDeleted = false;
        List<String> ids = approvalUserList.stream().map(ApprovalUser::getId).collect(Collectors.toList());
        if (!ids.isEmpty()) {
            isDeleted = this.deleteBatchIds(ids);
        }
        return isDeleted;
    }

    @Override
    public boolean updateContract(String companyId) {
        List<MemberListDTO> memberListDTOList = new ArrayList<>();
        List<CompanyDeptDTO> companyDeptDTOList = JSON.parseObject(redisTemplate.opsForList().range(ContactsCommon.ORG_COMPANY_DEPT_KEY + companyId, 0, -1).toString(), companyDeptType);
        for (CompanyDeptDTO companyDeptDTO : companyDeptDTOList) {
//              获取根部门下的成员列表
            if (redisTemplate.hasKey(ContactsCommon.ORG_DEPT_MEMBER_KEY + companyDeptDTO.getId())) {
                List<String> memberIdList = redisTemplate.opsForList().range(ContactsCommon.ORG_DEPT_MEMBER_KEY + companyDeptDTO.getId(), 0, -1);
                for (String memberId : memberIdList) {
                    MemberListDTO memberListDTO = getMemberListDTO(memberId);
                    if (memberListDTO != null) {
                        memberListDTOList.add(memberListDTO);
                    }
                }
            }
        }
        List<ApprovalUser> list = new ArrayList<>();
        for (MemberListDTO member : memberListDTOList) {
            ApprovalUser approvalUser = new ApprovalUser();
            approvalUser.setId(member.getMemberId());
            approvalUser.setOrgId(companyId);
//            orgMemeber.setDeptIds(new ArrayList<>(member.g));
//            orgMemeber.setDeptNames(member.getDeptNames());
            approvalUser.setName(member.getName());
            approvalUser.setMobile(member.getMobile());
            approvalUser.setPosition(member.getPosition());
            approvalUser.setAvatar(member.getProfile());
            approvalUser.setPassportId(member.getPassportId());
            list.add(approvalUser);
        }
        boolean b = this.addMember(list);
        return b;
    }

    /**
     * 获取成员信息
     *
     * @param memberId 成员id
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
