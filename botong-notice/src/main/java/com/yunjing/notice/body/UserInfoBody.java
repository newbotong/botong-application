package com.yunjing.notice.body;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 李双喜
 * @date 2018/3/22 14:11
 */
@Data
public class UserInfoBody implements Serializable{
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户头像
     */
    private String img;
    /**
     * 用户名称
     */
    private String name;
}
