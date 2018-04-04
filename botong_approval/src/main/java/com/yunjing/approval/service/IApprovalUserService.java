package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.mommon.global.exception.BaseException;

/**
 * @author 刘小鹏
 * @date 2018/03/26
 */
public interface IApprovalUserService extends IBaseService<ApprovalUser> {

    /**
     * 添加用户信息
     *
     * @param companyId 公司id
     * @param memberId  成员id
     * @return
     * @throws BaseException
     */
    boolean addUser(String companyId, String memberId) throws BaseException;


}
