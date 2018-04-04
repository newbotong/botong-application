package com.yunjing.approval.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 刘小鹏
 * @date 2017/11/22
 */
@Data
public class UserVO implements Serializable{

    /**
     * 用户主键
     */
    private String userId;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String userNick;

}