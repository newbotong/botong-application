package com.yunjing.approval.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liuxiaopeng
 * @date 2018/01/17
 */
@Data
public class ApprovalTemplVO implements Serializable {

    /**
     * 属性类型
     */
    private String id;

    /**
     * 属性中文名
     */
    private String cKey;

}
