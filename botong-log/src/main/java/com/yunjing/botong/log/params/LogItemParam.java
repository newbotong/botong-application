package com.yunjing.botong.log.params;

import lombok.Data;

/**
 * 日志项参数
 * @author 王开亮
 * @date 2018/4/9 9:41
 */
@Data
public class LogItemParam {

    /**
     * 项目字段ID
     */
    private String filedId;
    /**
     * 字段值
     */
    private String value;
}
