package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * 客户端--审批模型
 *
 * @author 刘小鹏
 * @date 2018/02/22
 */
@Data
public class ClientApprovalVO {

    /**
     * 审批主键
     */
    private Long approvalId;

    /**
     * 审批标题
     */
    private String title;

    /**
     * 审批logo
     */
    private String logo;

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
     * 审批人头像
     */
    private String userAvatar;

    /**
     * 审批人姓名
     */
    private String userNick;

    /**
     * 审批人主键
     */
    private Long userId;

    /**
     * 审批结果信息
     */
    private String message;

    /**
     * 审批创建时间
     */
    private Long createTime;
}
