package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
public class ModelDTO {

    /**
     * 主键
     */
    private String modelId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * logo
     */
    private String logo;

    /**
     * 功能介绍
     */
    private String introduce;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否禁用
     */
    private Integer isDisabled;

    /**
     * 是否 系统模型
     */
    private Integer isDef;

    /**
     * 提供者
     */
    private String provider;

    /**
     * 模型类型
     */
    private Integer modelType;

    /**
     * 模型版本
     */
    private Integer modelVersion;

}