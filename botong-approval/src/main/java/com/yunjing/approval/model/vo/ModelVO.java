package com.yunjing.approval.model.vo;

import com.yunjing.approval.model.entity.ModelL;
import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/03/22
 */
@Data
public class ModelVO {
    public ModelVO() {

    }

    public ModelVO(ModelL modelL) {
        this.modelId = modelL.getId();
        this.modelName = modelL.getModelName();
        this.logo = modelL.getLogo();
        this.visibleRange = modelL.getVisibleRange();
        this.introduce = modelL.getIntroduce();
        this.isDisabled = modelL.getIsDisabled();
        this.categoryId = modelL.getCategoryId();

    }

    /**
     * 模型主键
     */
    private Long modelId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * logo
     */
    private String logo;

    /**
     * 说明
     */
    private String introduce;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Integer isDisabled;

    /**
     * 分组主键
     */
    private Long categoryId;

    /**
     * 分组名称
     */
    private String categoryName;

    /**
     * 可见范围
     */
    private String visibleRange;

    /**
     * 审批人
     */
    private String approver;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 抄送人人数
     */
    private Integer copyUserCount;

    /**
     * 字段
     */
    private List<ModelItemVO> modelItems;
}
