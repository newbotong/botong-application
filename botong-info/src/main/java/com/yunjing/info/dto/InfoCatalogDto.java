package com.yunjing.info.dto;

import com.common.mybatis.model.BaseModel;
import lombok.Data;

import java.util.List;

/**
 * 资讯目录实体
 *
 * @author 裴志鹏
 * @date 2018/3/20 17:07
 */
@Data
public class InfoCatalogDto extends BaseModel<InfoCatalogDto> {

    /**
     * 名称
     */
    private String name;

    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 父id
     */
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
    private Integer isDelete;

    /**
     * 是否显示0：否，1：是
     */
    private Integer whetherShow;

    /**
     * 子集
     */
    private List<InfoCatalogDto> lower;
}
