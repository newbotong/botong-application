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
@TableName("approval_sets_condition")
@EqualsAndHashCode(callSuper = true)
public class SetsCondition extends BaseModel<SetsCondition> {

    /**
     * 模型主键
     */
    @TableField("model_id")
    private Long modelId;

    /**
     * 条件描述
     */
    @TableField("content")
    private String content;

    /**
     * 条件
     */
    @TableField("cdn")
    private String cdn;

    /**
     * 0: 无效 1: 有效
     */
    @TableField("enabled")
    private int enabled;

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
