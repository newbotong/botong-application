package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * 客户端--审批模型
 *
 * @author 刘小鹏
 * @date 2018/02/22
 */
@Data
public class ApprovalContentDTO {

    /**
     * 审批主键
     */
    private String approvalId;

    /**
     * 审批标题
     */
    private String title;

    /**
     * 审批logo
     */
    private String logo;

    /**
     * 审批模型主键
     */
    private String modelId;
    /**
     * 审批名称
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
     * 审批流程中的状态 0:未处理 1:同意 2:拒绝 3:转交 4.撤回
     */
    private Integer processState;

    /**
     * 审批人or抄送人头像
     */
    private String userAvatar;

    /**
     * 头像颜色
     */
    private String color;

    /**
     * 审批人or抄送人
     */
    private String userNick;

    /**
     * 审批人or抄送人主键
     */
    private String userId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 审批结果信息
     */
    private String message;

    /**
     * 审批创建时间
     */
    private Long createTime;

    /**
     * 审批抄送时间
     */
    private Long copysTime;

    /**
     * 抄送人是否已读
     */
    private Integer isRead;

    /**
     * 顺序
     */
    private Integer sort;
}
