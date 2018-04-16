package com.yunjing.info.dto;

import com.yunjing.info.model.InfoCatalog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 获取企业目录结构信息
 * @author 李双喜
 * @date 2018/4/8 10:34
 */
@Data
public class CompanyRedisCatalogDto implements Serializable,Comparable<CompanyRedisCatalogDto> {
    /**
     * id
     */
    private String id;

    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 名称
     */
    private String name;

    /**
     * 机构id
     */
    private String orgId;

    /**
     * 父id
     */
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
    private Integer isDelete;

    /**
     * 是否显示0：否，1：是
     */
    private Integer whetherShow;
    /**
     * 二级目录信息
     */
    private List<InfoCatalog> lower;
    /**
     * 排序
     * @param o  对象
     * @return
     */
    @Override
    public int compareTo(CompanyRedisCatalogDto o) {
        return this.getSort()-(o.getSort());
    }
}
