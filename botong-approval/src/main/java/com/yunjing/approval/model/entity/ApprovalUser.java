package com.yunjing.approval.model.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
@TableName("approval_user")
@EqualsAndHashCode(callSuper = true)
public class ApprovalUser extends BaseModel<ApprovalUser>{

    /**
     * 账号id
     */
    @TableField("passport_id")
    private String passportId;

    /**
     * 用户名
     */
    @TableField("name")
    private String name;

    /**
     * 企业主键
     */
    @TableField("org_id")
    private String orgId;

    /**
     * 用户头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 头像颜色
     */
    @TableField("color")
    private String color;

    /**
     * 用户手机号
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 部门主键
     */
    @TableField("dept_id")
    private String deptId;

    /**
     * 部门
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
