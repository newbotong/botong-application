package com.yunjing.botong.log.vo;

import lombok.Data;

/**
 * @author jingwj
 * @date 2018/3/30 17:06
 */
@Data
public class UserVO {
    /**
     * 成员id
     */
    private Long memberId;

    /**
     * 用户电话
     */
    private String userMobile;

    /**
     * 用户昵称
     */
    private String userNick;

    /**
     * 头像
     */
    private String profile;

}
