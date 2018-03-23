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
@TableName("approve_attr")
@EqualsAndHashCode(callSuper = true)
public class ApproveAttr extends Model<ApproveAttr> {

	@TableId("attr_id")
	private String attrId;

	@TableField("approval_id")
	private String approvalId;
	@TableField("attr_parent")
	private String attrParent;
	@TableField("attr_name")
	private String attrName;
	@TableField("attr_value")
	private String attrValue;
	@TableField("attr_num")
	private Integer attrNum;
	@TableField("attr_type")
	private Integer attrType;
	@TableField("attr_label")
	private String attrLabel;
	@TableField("attr_unit")
	private String attrUnit;
	@TableField("opt_value")
	private String optValue;

	@Override
	protected Serializable pkVal() {
		return this.attrId;
	}
}