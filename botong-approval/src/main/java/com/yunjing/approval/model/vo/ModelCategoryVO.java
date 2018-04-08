package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * 分组列表展示
 *
 * @author 刘小鹏
 * @date 2018/03/21
 */
@Data
public class ModelCategoryVO {

    /**
     * 分组主键
     */
    private Long categoryId;

    /**
     * 分组名称
     */
    private String categoryName;

    /**
     * 组内审批模型总数
     */
    private Integer modelCount;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 修改时间
     */
    private Long updateTime;
}
