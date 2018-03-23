package com.yunjing.sign.beans.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@TableName("sign_config_daily")
public class SignConfigDaily extends BaseModel<SignConfigDaily>  {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 主键
     */
	@TableField("is_open_photo")
	private Integer isOpenPhoto;
    /**
     * 是否允许微调
     */
	@TableField("is_distance")
	private Integer isDistance;
    /**
     * 微调距离
     */
	private Integer distance;
    /**
     * 是否开设置签到时间
     */
	@TableField("is_open_time")
	private Integer isOpenTime;
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
}
