package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
public class ApprovalDTO {

    /**
     * 审批ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 标题
     */
    private String title;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 完成时间
     */
    private Long finishTime;

    /**
     * 状态 0:审批中 1: 审批完成 2:已撤回
     */
    private Integer state;

    /**
     * 0:拒绝 1:同意
     */
    private Integer result;

    /**
     * 企业ID
     */
    private String orgId;

}
