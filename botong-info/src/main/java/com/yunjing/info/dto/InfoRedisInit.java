package com.yunjing.info.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 资讯初始化目录结构
 *
 * @author 李双喜
 * @date 2018/4/3 11:07
 */
@Data
public class InfoRedisInit implements Serializable {
    /**
     * 资讯目录一级id
     */
    private String id;
    /**
     * 资讯一级名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 等级
     */
    private Integer level;

    /**
     * 父级id
     */
    private String parentId;

}
