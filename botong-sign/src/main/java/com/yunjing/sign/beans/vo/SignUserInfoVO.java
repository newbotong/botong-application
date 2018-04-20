package com.yunjing.sign.beans.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
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
    private String memberId;

    /**
     * 姓名
     */
    @SerializedName(value = "memberName")
    private String name;

    /**
     * 头像
     */
    private String profile;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 颜色
     */
    private String color;
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
    private Integer signState;
}
