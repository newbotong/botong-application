package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 客户端--审批模型
 *
 * @author 刘小鹏
 * @date 2018/02/22
 */
@Data
public class ClientModelItemVO {

    public ClientModelItemVO() {

    }

    public ClientModelItemVO(ModelVO modelVO) {
        this.modelId = modelVO.getModelId();
        this.modelName = modelVO.getModelName();
        this.modelItems = modelVO.getModelItems();
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
     * modelItem
     */
    private List<ModelItemVO> modelItems;

    /**
     * 部门主键
     */
    private String deptId;

    /**
     * 部门名称
     */
    private String deptName;

}
