package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.mommon.global.exception.BaseException;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/03/26
 */
public interface IApprovalUserService extends IBaseService<ApprovalUser> {

    /**
     * 添加用户信息
     *
     * @param orgId 企业主键
     * @param userId 用户主键
     * @return
     * @throws BaseException
     */
    boolean addUser(String orgId,String userId) throws BaseException;



}
