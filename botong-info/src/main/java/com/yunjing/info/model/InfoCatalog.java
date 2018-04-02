package com.yunjing.info.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

/**
 * @author 裴志鹏
 * @date 2018/3/20 17:07
 */
@Data
@TableName("info_catalog")
public class InfoCatalog extends BaseModel<InfoCatalog> {

    /**
     * 名称
     */
    private String name;

    /**
     * 机构id
     */
    @TableField("org_id")
    private Long orgId;

    /**
     * 父id
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 级别
     */
    private Integer level;

    /**
     * 展示顺序
     */
    private Integer sort;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableField("is_delete")
    private Integer isDelete;

    /**
     * 是否显示0：否，1：是
     */
    @TableField("whether_show")
    private Integer whetherShow;
}
