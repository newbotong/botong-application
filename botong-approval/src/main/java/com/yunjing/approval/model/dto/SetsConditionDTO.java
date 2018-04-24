package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * @author lxp
 * @date 2017/12/21
 */
@Data
public class SetsConditionDTO {

    /**
     * 条件主键
     */
    private String conditions;

    /**
     * 模型主键
     */
    private String model;

    /**
     * 条件描述
     */
    private String content;

    /**
     * 条件
     */
    private String cdn;

    /**
     * 0: 无效 1: 有效
     */
    private int enabled;

    /**
     * 顺序
     */
    private int sort;

}
