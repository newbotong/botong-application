package com.yunjing.approval.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 成员列表dto
 *
 * @author 裴志鹏
 * @date 2018/3/26 14:08
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberListDTO implements Serializable {

    /**
     * 成员id
     */
    private String memberId;

    /**
     * 名称
     */
    private String name;

    /**
     * 头像
     */
    private  String profile;

    /**
     * 关心状态 0：未关心，1：关心
     */
    private String state;

    /**
     * 成员对应账户Id
     */
    private String passportId;

    /**
     * 电话号
     */
    private String mobile;

    /**
     * 职位
     */
    private String position;
}
