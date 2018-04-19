package com.yunjing.approval.model.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 企业模型
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
public class OrgModelDTO {

    /**
     * 企业模型主键
     */
    private String orgModelId;

    /**
     * 企业(组织)主键
     */
    private String orgId;

    /**
     * 模型主键
     */
    private String modelId;

    /**
     * 类型 1:日志 2:审批
     */
    private Integer dataType;

    /**
     * 创建时间
     */
    private Timestamp createTime;


}