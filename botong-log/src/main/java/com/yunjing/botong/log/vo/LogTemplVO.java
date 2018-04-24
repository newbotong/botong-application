package com.yunjing.botong.log.vo;

import lombok.Data;
import org.dozer.Mapping;

/**
 * @author liuxiaopeng
 * @date 2018/01/17
 */

@Data
public class LogTemplVO {

    /**
     * 属性编号
     */
    private String id;

    /**
     * 属性英文名
     */
    @Mapping("fieldName")
    private String eKey;

    /**
     * 属性中文名
     */
    @Mapping("fieldLabel")
    private String cKey;

    /**
     * 属性值
     */
    @Mapping("templateName")
    private String val;
}
