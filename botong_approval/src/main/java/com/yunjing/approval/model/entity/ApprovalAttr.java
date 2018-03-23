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
 * @date 2018/01/15
 */
@Data
@TableName("approval_attr")
@EqualsAndHashCode(callSuper = true)
public class ApprovalAttr extends Model<ApprovalAttr>{

    /**
     * 属性主键
     */
    @TableId("attr_id")
    private String attrId;

    /**
     * 审批主键
     */
    @TableField("approval_id")
    private String approvalId;

    /**
     * 属性名称
     */
    @TableField("attr_name")
    private String attrName;

    /**
     * 属性值
     */
    @TableField("attr_value")
    private String attrValue;

    /**
     * 父级主键
     */
    @TableField("attr_parent")
    private String attrParent;

    /**
     * 明细内的索引号, 从1开始
     */
    @TableField("attr_num")
    private Integer attrNum;

    /**
     * 属性类型
     */
    @TableField("attr_type")
    private Integer attrType;

    @Override
    protected Serializable pkVal() {
        return this.attrId;
    }
}
