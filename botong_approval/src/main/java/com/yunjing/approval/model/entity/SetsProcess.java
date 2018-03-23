package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lxp
 * @date 2017/12/21
 */
@Data
@TableName("approval_sets_process")
@EqualsAndHashCode(callSuper = true)
public class SetsProcess extends BaseModel<SetsProcess> {

    /**
     * 模型主键
     */
    @TableField("model")
    private Long modelId;

    /**
     * 条件主键
     */
    @TableField("condition_id")
    private Long conditionId;

    /**
     * 审批人
     */
    @TableField("approver")
    private String approver;

    /**
     * 顺序
     */
    @TableField("sort")
    private int sort;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
