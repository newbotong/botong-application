package com.yunjing.approval.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/3/5 9:56
 * @description:
 */
@Data
public class CompanyDTO  implements Serializable{

    private String id;

    /**
     * 公司名
     */
    private String name;

    /**
     * 创建人
     */
    private String ownerId;

    /**
     * 行业类型
     */
    private String industryType;

    /**
     * 所在地区
     */
    private String orgArea;


    private String groupId;

    /**
     * 企业简介
     */
    private String orgDesc;

    /**
     * 企业logo
     */
    private String orgLogo;
    /**
     * 企业开机启动图
     */
    private String orgImage;

    /**
     * 启动图跳转链接
     */
    private String orgUrl;

    /**
     * 起始时间
     */
    private Long startTime;
    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 是否开启邀请 0：开启；1：不开启
     */
    private Integer isOpenInvite;
    /**
     * 企业地址
     */
    private String address;
    /**
     * 联系方式
     */
    private String number;

    /**
     * 企业电话
     */
    private String phone;

    /**
     * 企业简称
     */
    private String shortName;

}
