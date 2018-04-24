package com.yunjing.sign.beans.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@Data
@EqualsAndHashCode(callSuper=true)
@TableName("sign_config_daily")
public class SignConfigDaily extends BaseModel<SignConfigDaily>  {

    private static final long serialVersionUID = 1L;

	@TableLogic
	@TableField("is_delete")
	private int isDelete;

	@TableField("org_id")
	private String orgId;

	/**
	 * 是否允许上传图片
	 */
	@TableField("is_open_photo")
	private Integer photoStatus;
	/**
	 * 是否允许微调
	 */
	@TableField("is_distance")
	private Integer distanceStatus;

	/**
	 * 微调距离
	 */
	@TableField("distance")
	private Integer distance;
	/**
	 * 是否开设置签到时间
	 */
	@TableField("is_open_time")
	private Integer timeStatus;
	/**
	 * 开启时间
	 */
	@TableField("start_time")
	private String startTime;
	/**
	 * 结束时间
	 */
	@TableField("end_time")
	private String endTime;

	/**
	 * 经度
	 */
	private String lng;

	/**
	 * 维度
	 */
	private String lat;

	/**
	 * 地址
	 */
	private String adress;

	/**
	 * 原点的打卡距离
	 */
	private Integer range;
}
