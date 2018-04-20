package com.yunjing.info.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 资讯V1.0目录实体
 *
 * @author 李双喜
 * @date 2018/3/20 17:07
 */
@Data
@TableName("content")
public class Content {

    /**
     * 内容id
     */
    @TableField("content_id")
    private String contentId;

    /**
     * 分类ID
     */
    @TableField("column_id")
    private String columnId;


    /**
     * 资讯标题
     */
    @TableField("content_title")
    private String contentTitle;

    /**
     * 内容子标题
     */
    @TableField("content_sub_title")
    private String contentSubTitle;


    /**
     * 资讯详情
     */
    @TableField("content_detail")
    private String contentDetail;


    /**
     * 资讯图片
     */
    @TableField("content_img")
    private String contentImg;

    /**
     * 展示顺序
     */
    @TableField("is_carousel")
    private Integer isCarousel;

    /**
     * 是否轮播 0轮播 1不轮播
     */
    @TableField("is_common")
    private Integer isCommon;

    /**
     *状态 0正常 1已删除
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否显示 0:否 1:是
     */
    @TableField("is_view")
    private Integer isView;

    /**
     * 操作人id
     */
    @TableField("operator_id")
    private String operatorId;
    /**
     * 展示顺序
     */
    @TableField("sort")
    private Integer sort;
    /**
     * 最后更改时间
     */
    @TableField("last_mdf_time")
    private Date lastMdfTime;
    /**
     * 发布时间
     */
    @TableField("create_time")
    private Date createTime;

}
