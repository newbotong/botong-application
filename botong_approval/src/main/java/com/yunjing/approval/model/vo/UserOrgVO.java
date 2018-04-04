package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * 通讯录信息——标签信息实体类
 *
 * @author 刘小鹏
 * @date 2018/04/04
 */
@Data
public class UserOrgVO {

    /**
     * 用户主键
     */
    private String userId;

    /**
     * 企业（组织）主键
     */
    private String orgId;

    /**
     * 职位
     */
    private String position;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;
}
