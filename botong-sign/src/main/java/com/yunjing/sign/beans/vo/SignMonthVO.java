package com.yunjing.sign.beans.vo;

import lombok.Data;

/**
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class SignMonthVO {

    private Long signTime;

    private Long userId;

    private String signDate;

    private Integer signWeek;

    private Integer signCount;
}
