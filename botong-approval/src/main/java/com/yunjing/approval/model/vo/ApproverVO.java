package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 审批人视图
 *
 * @author 刘小鹏
 * @date 2018/04/08
 */
@Data
public class ApproverVO {

    /**
     * 条件主键
     */
    private List<String> conditionId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门主键
     */
    private String deptId;

    private List<UserVO> approvers;

    private List<UserVO> copys;
}
