package com.yunjing.botong.log.params;

import lombok.Data;

/**
 * 日志模板枚举项定义
 *
 * @author  王开亮
 * @date 2018/3/30 16:25
 */
@Data
public class EnumItemParam {

    /**
     * 选项名称
     */
    private String name;

    /**
     * 选项值
     */
    private String value;

}
