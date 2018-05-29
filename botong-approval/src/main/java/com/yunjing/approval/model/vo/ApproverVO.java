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
    private String conditionId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门主键
     */
    private String deptId;

    /**
     * 是否为管理端设置的审批人
     */
    private String approverShow;

    /**
     * 管理端设置的审批人
     */
    private List<UserVO> approvers;

    /**
     * 管理端设置的抄送人
     */
    private List<UserVO> copys;

    /**
     * 最后一次提交审批时选择的审批人
     */
    private List<UserVO> lastApprovers;

    /**
     * 最后一次提交审批时选择的抄送人
     */
    private List<UserVO> lastCopys;
}
