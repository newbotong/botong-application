package com.yunjing.sign.beans.vo;

import lombok.Data;

import java.util.List;

/**
 * 签到和未签到列表
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class SignListVO {

    /**
     * 签到列表
     */
    private List<SignUserInfoVO> signList;

    /**
     * 未签到列表
     */
    private List<SignUserInfoVO> unSignList;

}
