package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 企业模型
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
@TableName("org_model")
@EqualsAndHashCode(callSuper=true)
public class OrgModel extends Model<OrgModel> {

	/**
	 * 企业模型主键
	 */
	@TableId(value = "orgmodel_id", type = IdType.UUID)
	private String orgModelId;

	/**
	 * 企业(组织)主键
	 */
	@TableField("org_id")
	private String orgId;

	/**
	 * 模型主键
	 */
	@TableField("model_id")
	private String modelId;

	/**
	 * 类型 1:日志 2:审批
	 */
	@TableField("data_type")
	private Integer dataType;

	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private Timestamp createTime;


	@Override
	protected Serializable pkVal() {
		return this.orgModelId;
	}
}