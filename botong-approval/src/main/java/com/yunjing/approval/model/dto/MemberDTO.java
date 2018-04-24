package com.yunjing.approval.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/3/2 15:08
 * @description:
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDTO implements Serializable {

    /**
     * 成员id
     */
    private String id;
    /**
     * 成员对应账户Id
     */
    private String passportId;
    /**
     * 成员所在企业Id
     */
    private String companyId;

    /**
     * 总公司id
     */
    private String companyHeadquartersId;
    /**
     * 地址
     */
    private String position;
    /**
     * 电话号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 成员所在部门Id集合
     */
    private Set<String> deptIds;

    /**
     * 性别 0:女 1:男
     */
    private Integer gender;

    /**
     * 头像
     */
    private String profile;

    /**
     * 名称
     */
    private String name;

}
