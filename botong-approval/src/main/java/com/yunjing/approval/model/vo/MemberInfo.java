package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * <p> 成员信息
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/30.
 */
@Data
public class MemberInfo {

    /**
     * id
     */
    private String id;

    /**
     * 用户id
     */
    private String passportId;

    /**
     * 名称
     */
    private String name;

    /**
     * 成员所在企业Id
     */
    private String companyId;
    /**
     * 职位
     */
    private String position;
    /**
     * 电话号
     */
    private String mobile;

    /**
     * 部门id集合
     */
    private List<String> deptIds;

    /**
     * 部门名称集合
     */
    private List<String> deptNames;
}
