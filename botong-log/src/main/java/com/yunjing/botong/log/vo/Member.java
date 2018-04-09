package com.yunjing.botong.log.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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
    protected String memberName;

    /**
     * 创建时间
     */
    protected long createTime;
    
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
