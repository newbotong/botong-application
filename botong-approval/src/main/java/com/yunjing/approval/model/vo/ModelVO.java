package com.yunjing.approval.model.vo;

import com.yunjing.approval.model.entity.ModelItem;
import com.yunjing.approval.model.entity.ModelL;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author 刘小鹏
 * @date 2018/03/22
 */
@Data
public class ModelVO {
    public ModelVO() {

    }

    public ModelVO(ModelL modelL) {
        this.modelId = String.valueOf(modelL.getId());
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
    private String categoryId;

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
     * 是否有必填的子项
     */
    private Boolean haveRequired;

    /**
     * 字段
     */
    private List<ModelItemVO> items;

    /**
     * 需要必填的单选框或数字框模型子项
     */
    private Set<ModelItem> itemVOSet;

    /**
     * 是否已设置审批人
     */
    private Boolean isSetApprover;
}
