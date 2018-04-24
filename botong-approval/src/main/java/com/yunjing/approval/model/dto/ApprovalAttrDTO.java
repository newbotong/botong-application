package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2018/01/15
 */
@Data
public class ApprovalAttrDTO {

    /**
     * 属性主键
     */
    private String attrId;

    /**
     * 审批主键
     */
    private String approvalId;

    /**
     * 属性名称
     */
    private String attrName;

    /**
     * 属性值
     */
    private String attrValue;

    /**
     * 父级主键
     */
    private String attrParent;

    /**
     * 明细内的索引号, 从1开始
     */
    private Integer attrNum;

    /**
     * 属性类型
     */
    private Integer attrType;

}
