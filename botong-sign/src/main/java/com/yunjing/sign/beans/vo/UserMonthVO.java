package com.yunjing.sign.beans.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 我签到的详情
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class UserMonthVO {

    private String userName;

    private String deptName;

    private String postName;

    private Map<String, SignMonthVO> monthList;

}
