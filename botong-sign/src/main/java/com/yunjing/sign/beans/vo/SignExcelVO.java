package com.yunjing.sign.beans.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 导出签到数据视图
 * @author jingwj
 * @date 2018/03/24
 */
@Data
public class SignExcelVO {

    private String userId;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 部门
     */
    private String deptName;

    /**
     * 完整部门
     */
    private String path;

    /**
     * 职位
     */
    private String position;

    /**
     * 签到日期
     */
    private Long signDate;

    /**
     * 签到时间
     */
    private String signTime;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 签到地点
     */
    private String addressTitle;

    /**
     * 签到详细地址
     */
    private String address;

    /**
     * 备注
     */
    private String remark;

    /**
     * 签到设备
     */
    private String device;

    private String imgUrls;

    /**
     * 签到照片
     */
    private List<AttrValueVO> listValue;

}
