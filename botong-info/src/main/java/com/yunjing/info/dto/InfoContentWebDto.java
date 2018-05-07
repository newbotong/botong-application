package com.yunjing.info.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * web端资讯详情接口
 *
 * @author 李双喜
 * @date 2018/5/7 15:27
 */
@Data
public class InfoContentWebDto implements Serializable {

    /**
     * 目录id
     */
    private String catalogId;

    /**
     * 机构id
     */
    private String orgId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片地址
     */
    private String pictureUrl;

    /**
     * 内容
     */
    private String content;
    /**
     *
     */
    private Integer readNumber;

    /**
     * 是否轮播 0：否，1：是
     */
    private Integer whetherShow;

    /**
     * 展示顺序
     */
    private Integer sort;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    private Integer isDelete;
    /**
     * 类目名称
     */
    private String catalogName;
}
