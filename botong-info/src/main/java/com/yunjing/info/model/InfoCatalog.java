package com.yunjing.info.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资讯目录实体
 *
 * @author 李双喜
 * @date 2018/3/20 17:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("info_catalog")
public class InfoCatalog extends BaseModel<InfoCatalog> implements Comparable<InfoCatalog>{

    /**
     * 名称
     */
    private String name;

    /**
     * 机构id
     */
    @TableField("org_id")
    private String orgId;

    /**
     * 父id
     */
    @TableField("parent_id")
    private String parentId;

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

    /**
     * 排序
     * @param o 目录结构排序入参
     * @return
     */
    @Override
    public int compareTo(InfoCatalog o) {
        return this.getSort()- o.getSort();
    }
}
