package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 客户端--审批详情视图
 *
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
public class ClientApprovalDetailVO {

    /**
     * 审批发起人id
     */
    private String memberId;

    /**
     * 审批发起人账号id
     */
    private String passportId;

    /**
     * 审批发起人姓名
     */
    private String name;

    /**
     * 发起人头像
     */
    private String avatar;

    /**
     * 发起人头像上的名字
     */
    private String avatarName;

    /**
     * 头像背景颜色
     */
    private String color;

    /**
     * 发起人部门
     */
    private String deptName;

    /**
     * 发起人职位
     */
    private String position;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 审批状态 0:审批中 1:审批完成 2:已撤回
     */
    private Integer state;

    /**
     * 审批结果 1:已同意 2:已拒绝 4:已撤销
     */
    private Integer result;

    /**
     * 审批流程状态
     */
    private Integer processState;

    /**
     * 审批流程状态信息
     */
    private String message;

    /**
     * 审批模型中输入详情
     */
    private List<ApproveAttrVO> approveAttrVO;

    /**
     * 审批情况集合
     */
    private List<ApprovalUserVO> approvalUserList;

    /**
     * 抄送情况集合
     */
    private List<CopyUserVO> copyUserList;
}
