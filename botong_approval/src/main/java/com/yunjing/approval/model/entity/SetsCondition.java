package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author lxp
 * @date 2017/12/21
 */
@Data
@TableName("approval_sets_condition")
@EqualsAndHashCode(callSuper = true)
public class SetsCondition extends Model<SetsCondition> {

    /**
     * 条件主键
     */
    @TableId("conditions")
    private String conditions;

    /**
     * 模型主键
     */
    @TableField("model")
    private String model;

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

    @Override
    protected Serializable pkVal() {
        return this.conditions;
    }
}
