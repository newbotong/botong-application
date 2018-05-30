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
     * 管理端设置的默认审批人列表
     */
    private List<UserVO> approverVOS;

    /**
     * 管理端设置的默认抄送人列表
     */
    private List<UserVO> copyerVOS;

    /**
     * 最后一次提交审批时选择的审批人
     */
    private List<UserVO> lastApprovers;

    /**
     * 最后一次提交审批时选择的抄送人
     */
    private List<UserVO> lastCopys;

    /**
     * 是否为管理端设置的审批人
     */
    private String approverShow;

}
