package com.yunjing.info.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 资讯搜索分页DTO
 *
 * @author 李双喜
 * @date 2018/4/3 10:26
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoContentDto implements Serializable {
    /**
     * 资讯id
     */
    private Long id;

    /**
     * 公司id
     */
    private Long orgId;

    /**
     * 目录id
     */
    private Long catalogId;

    /**
     * 目录名称
     */
    private String catalogName;


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
     * 阅读数量
     */
    private Integer readNumber;

    /**
     * 是否显示0：否，1：是
     */
    private Integer whetherShow;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

}
