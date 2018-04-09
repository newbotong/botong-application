package com.yunjing.sign.beans.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 2018/3/20
 * @description
 **/
@Data
@TableName("sign_config")
@EqualsAndHashCode(callSuper=true)
public class SignConfigModel extends BaseModel<SignConfigModel> {

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

}
