package com.yunjing.notice.body;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息body
 *
 * @author 李双喜
 * @date 2018/3/22 14:11
 */
@Data
public class UserInfoBody implements Serializable {

    /**
     * 用户id
     */
    private String id;
    /**
     * 用户头像
     */
    private String img;

    /**
     * 用户颜色
     */
    private String color;
    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户手机号码
     */
    private Long phone;
}
