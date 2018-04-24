package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
public class ApproveAttributeVO {

    /**
     * 审批信息主键
     */
    private String id;
    /**
     * 审批主键
     */
    private String approvalId;

    /**
     * 父属性主键
     */
    private String attrParent;

    /**
     * 属性名称
     */
    private String attrName;

    /**
     * 属性值
     */
    private String attrValue;

    /**
     * 索引号
     */
    private Integer attrNum;

    /**
     * 类型
     */
    private Integer attrType;

    /**
     * 字段
     */
    private String attrLabel;
    /**
     * 字段
     */
    private String attrLabels;

    /**
     * 单位
     */
    private String attrUnit;

    /**
     * 可选项
     */
    private String optValue;

    /**
     * 企业主键
     */
    private String orgId;

    /**
     * 时间格式
     */
    private String dateFormat;




}