package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.ApprovalUser;

import java.util.List;

/**
 * 审批mapper
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
public interface ApprovalUserMapper extends IBaseMapper<ApprovalUser> {

    /**
     * 根据部门查询
     * @param deptId 部门主键
     * @return
     */
    List<ApprovalUser> selectUser(String deptId);

}
