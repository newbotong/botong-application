package com.yunjing.sign.beans.vo;

import lombok.Data;

import java.util.List;

/**
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class SignDateVO {

    /**
     * 签到时间
     */
    private String signDate;

    /**
     * 明细列表
     */
    private List<SignDetailVO> detailList;
}
