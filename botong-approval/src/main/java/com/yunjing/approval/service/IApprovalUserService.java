package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.dto.OrgMemberMessage;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.mommon.global.exception.BaseException;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/03/26
 */
public interface IApprovalUserService extends IBaseService<ApprovalUser> {

    /**
     * 添加成员信息
     *
     * @param orgMemberMessages 企业成员信息集合
     * @return
     * @throws BaseException
     */
    boolean addMember(List<OrgMemberMessage> orgMemberMessages);

    /**
     * 更新成员信息
     *
     * @param orgMemberMessages 企业成员信息集合
     * @return
     * @throws BaseException
     */
    boolean updateMember(List<OrgMemberMessage> orgMemberMessages);

    /**
     * 删除成员信息
     *
     * @param orgMemberMessages 企业成员信息集合
     * @return
     * @throws BaseException
     */
    boolean deleteMember(List<OrgMemberMessage> orgMemberMessages);


    /**
     * 手动更新联系人信息
     *
     * @param companyId
     * @return boolean
     */
    boolean updateContract(String companyId,String userId,Integer choiceContacts);
}
