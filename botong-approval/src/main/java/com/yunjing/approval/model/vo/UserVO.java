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
     * 成员id
     */
    private String memberId;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 审批人手机号
     */
    private String mobile;

    /**
     * 审批人名称
     */
    private String name;

    /**
     * 账号id
     */
    private String passpottld;

    /**
     * 头像
     */
    private String profile;
    /**
     * 头像颜色
     */
    private String color;

}