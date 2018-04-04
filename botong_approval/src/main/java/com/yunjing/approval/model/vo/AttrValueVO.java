package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2018/04/04
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
