package com.yunjing.sign.beans.vo;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

/**
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class SignConfigVO {


    /**
     * 组织id
     */
    private String orgId;


    private Integer photoStatus;

    /**
     * 是否可以微调
     */
    private Integer distanceStatus;

    /**
     * 微调距离
     */
    private Integer distance;

    /**
     * 是否可以需要设置时间
     */
    private Integer timeStatus;

    /**
     * 开启时间
     */
    private String startTime;

    /**
     * 结束时间
     */
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
