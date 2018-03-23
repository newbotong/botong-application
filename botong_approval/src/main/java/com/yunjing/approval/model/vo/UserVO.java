package com.yunjing.approval.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lxp
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }


    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar == null ? null : userAvatar.trim();
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick == null ? null : userNick.trim();
    }

}