package com.yunjing.info.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

/**
 * 资讯内容实体
 *
 * @author 裴志鹏
 * @date 2018/3/20 17:15
 */
@Data
@TableName("info_content")
public class InfoContent extends BaseModel<InfoContent> {
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
