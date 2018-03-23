package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * @author liuxiaopeng
 * @date 2018/01/17
 */
@Data
public class AttrValueVO {

    /**
     * 英文名
     */
    private String ekey;
    /**
     * 中文名
     */
    private String ckey;
    /**
     * 属性值
     */
    private String attrVal;

}
