package com.yunjing.sign.beans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liuxiaopeng
 * @date 2018/01/17
 */
@Data
public class SignTemplVO implements Serializable {

    /**
     * 属性编号
     */
    private String id;

    /**
     * 属性英文名
     */
    private String eKey;

    /**
     * 属性中文名
     */
    private String cKey;

    /**
     * 属性值
     */
    private String val;

}
