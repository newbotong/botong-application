package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 *
 * @author 刘小鹏
 * @date 2017/12/21
 */
@Data
@TableName("approval_sets")
@EqualsAndHashCode(callSuper = true)
public class ApprovalSets extends Model<ApprovalSets>{

    /**
     * 模型主键
     */
    @TableId("model_id")
    private String modelId;

    /**
     * 0:不分条件设置审批人 1:分条件设置审批人
     */
    @TableField("setting")
    private Integer setting;

    @Override
    protected Serializable pkVal() {
        return this.modelId;
    }
}
