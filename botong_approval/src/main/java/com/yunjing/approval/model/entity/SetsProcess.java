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
@TableName("approval_sets_process")
@EqualsAndHashCode(callSuper = true)
public class SetsProcess extends Model<SetsProcess> {

    /**
     * 流程主键
     */
    @TableId("process")
    private String process;

    /**
     * 模型主键
     */
    @TableField("model")
    private String model;

    /**
     * 条件主键
     */
    @TableField("conditions")
    private String conditions;

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

    @Override
    protected Serializable pkVal() {
        return this.process;
    }
}
