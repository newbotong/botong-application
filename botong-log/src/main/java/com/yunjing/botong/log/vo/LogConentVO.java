package com.yunjing.botong.log.vo;

import lombok.Data;

/**
 * @author jingwj
 * @date 2018/3/30 17:06
 */
@Data
public class LogConentVO {
    /**
     * 日志模板字段Id
     */
    private String id;

    /**
     * 日志模板列名
     */
    private String name;

    /**
     * 显示值
     */
    private String value;

    /**
     * 日志模板列类型
     */
    private String type;

    /**
     * 枚举ID,单选和复选
     */
    private Long enumId;

    /**
     * 枚举值ID,单选和复选
     */
    private Long enumValueId;

    /**
     * 枚举值
     */
    private String enumValue;

    /**
     * 枚举显示值
     */
    private String enumName;

}
