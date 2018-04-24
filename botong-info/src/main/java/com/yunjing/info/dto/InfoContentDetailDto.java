package com.yunjing.info.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 资讯详情DTO
 *
 * @author 李双喜
 * @date 2018/4/3 9:59
 */
@Data
public class InfoContentDetailDto implements Serializable {
    /**
     * 资讯id
     */
    private String id;
    /**
     * 标题
     */
    private String title;
    /**
     * 图片地址
     */
    private String pictureUrl;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 创建日期
     */
    private Long createTime;

    /**
     * 收藏id
     */
    private String favId;
    /**
     * 是否收藏   true为已收藏    false为未收藏
     */
    private Boolean favouriteState;
    
}
