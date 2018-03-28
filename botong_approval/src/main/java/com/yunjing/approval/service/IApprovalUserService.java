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
     * @param orgId 企业主键
     * @return
     */
    boolean addUser(Long orgId) throws BaseException;

}
