package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/04/04
 */
@Data
public class ApprovalExcelVO {

    /**
     * 版本
     */
    private Integer modelVersion;

    /**
     * 模型主键
     */
    private Long modelId;
    /**
     * 审批标题
     */
    private String title;
    /**
     * 审批状态
     */
    private String state;
    /**
     * 审批结果
     */
    private String result;
    /**
     * 审批发起时间
     */
    private Long createTime;
    /**
     * 审批结束时间
     */
    private Long finishTime;
    /**
     * 发起人姓名
     */
    private String userName;
    /**
     * 发起人部门
     */
    private String deptName;
    /**
     * 审批人姓名
     */
    private String approvalName;
    /**
     * 耗时
     */
    private String timeConsuming;

    /**
     * 属性值
     */
    private List<AttrValueVO> listValue;

}
