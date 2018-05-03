package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
@Data
public class ApprovalSetsDTO {

    /**
     * 模型主键
     */
    private String modelId;

    /**
     * 0:不分条件设置审批人 1:分条件设置审批人
     */
    private Integer setting;
}
