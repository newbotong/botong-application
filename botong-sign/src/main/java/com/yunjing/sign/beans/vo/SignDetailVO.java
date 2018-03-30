package com.yunjing.sign.beans.vo;

import lombok.Data;

/**
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class SignDetailVO {

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 统计日期
     */
    private Long signDate;


    /**
     * 地址头
     */
    private String addressTitle;

    /**
     * 签到图片
     */
    private String imgUrls;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 备注
     */
    private String remark;
}
