package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
@TableName("approve_attr")
@EqualsAndHashCode(callSuper = true)
public class ApproveAttr extends BaseModel<ApproveAttr> {

	/**
	 * 审批主键
	 */
	@TableField("approval_id")
	private String approvalId;

	/**
	 * 父属性主键
	 */
	@TableField("attr_parent")
	private String attrParent;

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
	 * 索引号
	 */
	@TableField("attr_num")
	private Integer attrNum;

	/**
	 * 类型
	 */
	@TableField("attr_type")
	private Integer attrType;

	/**
	 * 字段
	 */
	@TableField("attr_label")
	private String attrLabel;

	/**
	 * 单位
	 */
	@TableField("attr_unit")
	private String attrUnit;

	/**
	 * 可选项
	 */
	@TableField("opt_value")
	private String optValue;

	/**
	 * 是否删除 0：未删除；1：已删除
	 */
	@TableLogic
	@TableField("is_delete")
	private Integer isDelete;
}