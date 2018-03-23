package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * 客户端--审批模型
 *
 * @author 刘小鹏
 * @date 2018/02/22
 */
@Data
public class ClientModelVO {

    public ClientModelVO() {

    }

    public ClientModelVO(ModelVO modelVO) {
        this.modelId = modelVO.getModelId();
        this.modelName = modelVO.getModelName();
        this.logo = modelVO.getLogo();
        this.isDisabled = modelVO.getIsDisabled();

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
     * 是否启用
     */
    private Integer isDisabled;

}
