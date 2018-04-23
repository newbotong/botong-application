package com.yunjing.approval.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 裴志鹏
 * @date 2018/3/22 16:30
 */
@Data
public class CompanyDeptDTO implements Serializable{

    /**
     * 部门id
     */
    private String id;

    /**
     * 组织类型 0：子公司，1：合作公司，2：部门，3：根部门
     */
    private String orgType;
}
