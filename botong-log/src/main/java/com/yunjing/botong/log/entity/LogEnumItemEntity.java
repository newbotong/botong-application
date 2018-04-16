package com.yunjing.botong.log.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志实体中的枚举项定义
 * @author auth
 * @date 2018/4/9 16:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEnumItemEntity {

    /**
     *
     */
    private String id;
    /**
     *
     */
    private String itemKey;
    /**
     *
     */
    private String itemValue;

}
