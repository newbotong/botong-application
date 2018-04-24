package com.yunjing.sign.beans.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
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
@TableName("sign_detail_img")
public class SignDetailImg  extends BaseModel<SignDetailImg> {

    private static final long serialVersionUID = 1L;

    /**
     * 签到明细id
     */
	@TableField("sign_detail_id")
	private String signDetailId;
    /**
     * 图片地址
     */
	private String url;
    /**
     * 图片排序
     */
	private Integer sort;

}
