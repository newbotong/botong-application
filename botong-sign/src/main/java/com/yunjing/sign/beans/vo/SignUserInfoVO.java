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
     * 用户id
     */
    private Long memberId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String profile;

    /**
     * 部门
     */
    private String[] deptNames;



    /**
     * 职位
     */
    private String position;

    /**
     * 签到状态
     */
    private int signState;
}
