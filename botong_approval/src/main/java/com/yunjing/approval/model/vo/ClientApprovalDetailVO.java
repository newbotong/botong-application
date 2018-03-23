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
     * 审批发起人姓名
     */
    private String name;

    /**
     * 发起人头像
     */
    private String avatar;

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
     * 审批状态
     */
    private Integer state;

    /**
     * 审批模型中输入详情
     */
    private List<InputDetailVO> inputDetailList;

    /**
     * 审批情况集合
     */
    private List<ApprovalUserVO> approvalUserList;

    /**
     * 抄送情况集合
     */
    private List<CopyUserVO> copyUserList;
}
