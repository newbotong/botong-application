package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * 审批详情dto
 * @author 刘小鹏
 * @date 2018/03/26
 */
@Data
public class ApprovalDetailDTO {

    /**
     * 审批模型主键
     */
    private String modelId;

    /**
     * 审批模型名称
     */
    private String modelName;

    /**
     * 审批属性code
     */
    private String field;

    /**
     * 审批属性name
     */
    private String itemLabel;

    /**
     * 审批项主键
     */
    private String modelItemId;

    /**
     * 排序
     */
    private Integer priority;

    /**
     * 数据类型
     */
    private Integer dataType;

    /**
     * 审批项子项
     */
    private String isChild;

    /**
     * 审批属性值
     */
    private String attrValue;

    /**
     * 审批属性默认值
     */
    private String optValue;

    /**
     * 单位
     */
    private String unit;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 用户主键
     */
    private String userId;

    /**
     * 用户名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 颜色
     */
    private String color;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 审批状态 0:审批中 1:审批完成 2:已撤回
     */
    private Integer state;

    /**
     * 审批结果 1:已同意 2:已拒绝 4:已撤销
     */
    private Integer result;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 职位
     */
    private String position;
    /**
     * 流程审批状态
     */
    private Integer processState;
}
