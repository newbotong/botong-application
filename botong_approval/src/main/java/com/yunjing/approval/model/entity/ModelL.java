package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
@TableName("model")
@EqualsAndHashCode(callSuper=true)
public class ModelL extends Model<ModelL> {

	/**
	 * 主键
	 */
	@TableId(value = "model_id", type = IdType.UUID)
	private String modelId;

	/**
	 * 模型名称
	 */
	@TableField("model_name")
	private String modelName;

	/**
	 * logo
	 */
	@TableField("logo")
	private String logo;

	/**
	 * 功能介绍
	 */
	@TableField("introduce")
	private String introduce;

	/**
	 * 排序
	 */
	@TableField("sort")
	private Integer sort;

	/**
	 * 是否停用(0:启用，1：停用)
	 */
	@TableField("is_disabled")
	private Integer isDisabled;

	/**
	 * 是否 系统模型
	 */
	@TableField("is_def")
	private Integer isDef;

	/**
	 * 提供者
	 */
	@TableField("provider")
	private String provider;

	/**
	 * 模型类型
	 */
	@TableField("model_type")
	private Integer modelType;

	/**
	 * 模型版本
	 */
	@TableField("model_version")
	private Integer modelVersion;

	/**
	 * 所属分组
	 */
	@TableField("category_id")
	private Long categoryId;

	/**
	 * 可见范围
	 */
	@TableField("visible_range")
	private String visibleRange;

	/**
	 * 是否删除
	 */
	@TableField("is_delete")
	private Integer isDelete;

	@Override
	protected Serializable pkVal() {
		return this.modelId;
	}
}