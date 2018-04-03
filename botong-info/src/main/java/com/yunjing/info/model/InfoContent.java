package com.yunjing.info.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

/**
 * @author 裴志鹏
 * @date 2018/3/20 17:15
 */
@Data
@TableName("info_content")
public class InfoContent extends BaseModel<InfoContent> {
    /**
     * CREATE TABLE `info_content` (
     `id` bigint(22) NOT NULL COMMENT '主键id',
     `catalog_id` bigint(22) DEFAULT NULL COMMENT '目录id',
     `org_id` bigint(22) DEFAULT NULL COMMENT '公司id',
     `department_name` bigint(22) DEFAULT NULL COMMENT '部门id',
     `title` varchar(35) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
     `picture_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
     `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '内容',
     `read_number` int(11) DEFAULT '0' COMMENT '阅读数量',
     `whether_show` tinyint(1) DEFAULT '1' COMMENT '是否显示0：否，1：是',
     `sort` tinyint(6) DEFAULT '0' COMMENT '展示顺序',
     `is_delete` tinyint(1) DEFAULT NULL COMMENT '是否删除 0：未删除；1：已删除',
     `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
     `update_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
     PRIMARY KEY (`id`)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯内容';
     */

    /**
     * 目录id
     */
    @TableField("catalog_id")
    private Long catalogId;

    /**
     * 机构id
     */
    @TableField("org_id")
    private Long orgId;

    /**
     * 部门名称
     */
    @TableField("department_name")
    private String departmentName;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 图片地址
     */
    @TableField("picture_url")
    private String pictureUrl;

    /**
     * 内容
     */
    @TableField("content")
    private String content;
    /**
     *
     */
    @TableField("read_number")
    private Integer readNumber;

    /**
     * 是否轮播 0：否，1：是
     */
    @TableField("whether_show")
    private Integer whetherShow;

    /**
     * 展示顺序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableField("is_delete")
    private Integer isDelete;
}
