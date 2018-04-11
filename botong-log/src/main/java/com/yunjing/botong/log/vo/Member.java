package com.yunjing.botong.log.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/3/2 15:08
 * @description:
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member implements Serializable {

    public Member() {
    }

    public Member(String id) {
        this.id = id;
    }


    public Member(String id, String name, String mobile, String profile) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.profile = profile;
    }

    @SerializedName(value = "memberId")
    protected String id;

    /**
     * 父节点编号
     */
    protected String parentId;

    /**
     * 排序
     */
    protected Integer sort;

    /**
     * 成员名称
     */
    @SerializedName(value = "memberName")
    protected String name;

    /**
     * 创建时间
     */
    protected Long createTime;

    /**
     * 成员对应账户Id
     */
    private String passportId;

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
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String profile;
    /**
     * 成员所在部门Id集合
     */
    private Set<String> deptIds;
    /**
     * 总公司id
     */
    private String companyHeadquartersId;
    /**
     * 部门名
     */
    private List<String> deptNames;

    /**
     * 头像颜色
     */
    private String color;
}
