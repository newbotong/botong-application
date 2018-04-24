package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

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
        this.items = modelVO.getItems();
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
    private List<ModelItemVO> items;

    /**
     * 部门
     */
    private List<DeptVO> deptList;

    /**
     * 审批流程设置  0:不分条件设置审批人 1:分条件设置审批人
     */
    private Integer set;

    /**
     * 审批人列表
     */
    private List<UserVO> approverVOS;

    /**
     * 抄送人列表
     */
    private List<UserVO> copyerVOS;

    private Set<String> field;
}
