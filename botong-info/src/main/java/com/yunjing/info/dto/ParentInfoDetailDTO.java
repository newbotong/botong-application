package com.yunjing.info.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 资讯首页列表DTO
 *
 * @author 李双喜
 * @date 2018/4/4 11:30
 */
@Data
public class ParentInfoDetailDTO implements Serializable,Comparable<ParentInfoDetailDTO>{

    /**
     * 资讯id
     */
    private Long id;
    /**
     * 一级目录id
     */
    private Long catalogId;
    /**
     * 一级目录名称
     */
    private String name;

    /**
     * 目录sort
     */
    private Integer catalogSort;

    /**
     * 标题
     */
    private String title;
    /**
     * 图片地址
     */
    private String pictureUrl;
    /**
     * 创建日期
     */
    private Long createTime;

    @Override
    public int compareTo(ParentInfoDetailDTO o) {
        return this.getCatalogSort()-o.getCatalogSort();
    }
}
