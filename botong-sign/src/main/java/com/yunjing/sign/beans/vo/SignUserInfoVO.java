package com.yunjing.sign.beans.vo;

import lombok.Data;

/**
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class SignUserInfoVO {


    /**
     * 组织id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 头像
     */
    private String userIcon;

    /**
     * 签到状态
     */
    private int signState;
}
