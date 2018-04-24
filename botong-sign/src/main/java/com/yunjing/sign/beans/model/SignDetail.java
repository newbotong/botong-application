package com.yunjing.sign.beans.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 签到明细
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@Data
@TableName("sign_detail")
@EqualsAndHashCode(callSuper=true)
public class SignDetail extends BaseModel<SignDetail> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 机构id
     */
	@TableField("user_id")
	private String userId;
    /**
     * 签到地址标头
     */
	@TableField("address_title")
	private String addressTitle;
    /**
     * 签到详细地址
     */
	private String address;
    /**
     * 签到经度
     */
	private String lng;
    /**
     * 签到纬度
     */
	private String lat;
    /**
     * 备注
     */
	private String remark;
    /**
     * 签到次数
     */
	private Integer count;

}
