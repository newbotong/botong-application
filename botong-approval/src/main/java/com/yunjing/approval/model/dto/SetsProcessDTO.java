package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * @author lxp
 * @date 2017/12/21
 */
@Data
public class SetsProcessDTO {

    /**
     * 流程主键
     */
    private String process;

    /**
     * 模型主键
     */
    private String model;

    /**
     * 条件主键
     */
    private String conditions;

    /**
     * 审批人
     */
    private String approver;

    /**
     * 顺序
     */
    private int sort;

}
