package com.yunjing.sign.beans.vo;

import lombok.Data;

import java.util.List;

/**
 * 我签到的详情
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class MySignVO {

    private Long userId;

    private String userName;

    private Integer signCount;

    /**
     * 签到列表
     */
    private List<SignDateVO> signList;
}
