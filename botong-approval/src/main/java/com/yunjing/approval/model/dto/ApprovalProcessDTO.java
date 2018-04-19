package com.yunjing.approval.model.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 刘小鹏
 * @date 2018/1/15
 */
@Data
public class ApprovalProcessDTO {

    /**
     * 流程主键
     */
    private String processId;

    /**
     * 审批主键
     */
    private String approvalId;

    /**
     * 审批人主键
     */
    private String userId;

    /**
     * 状态 0:未处理 1:同意 2:拒绝 3:转交 4.撤回
     */
    private Integer state;

    /**
     * 排序
     */
    private Integer seq;

    /**
     * 模型ID
     */
    private Timestamp processTime;

    /**
     * 标题
     */
    private String reason;

}
