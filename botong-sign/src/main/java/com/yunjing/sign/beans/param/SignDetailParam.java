package com.yunjing.sign.beans.param;

import com.yunjing.mommon.validate.annotation.Size;
import lombok.Data;

import java.util.List;

/**
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class SignDetailParam {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 组织Id
     */
    private Long orgId;

    /**
     * 统计日期
     */
    private String signDate;

    /**
     * 经度
     */
    private String lng;

    /**
     * 维度
     */
    private String lat;

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
