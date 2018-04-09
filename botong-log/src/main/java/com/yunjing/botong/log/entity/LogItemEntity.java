package com.yunjing.botong.log.entity;

import java.util.List;

/**
 * 日志内容项目实体
 * @author 王开亮
 * @date 2018/4/9 16:24
 */
public class LogItemEntity {

    /**
     * 字段ID
     */
    private String id;
    /**
     * 日志模板字段显示名称
     */
    private String name;
    /**
     * 日志对应字段值
     */
    private String value;
    /**
     * 字段类型 1-多行输入框 2-数字输入框 3-单选框 4-日期 5-日期区间 6-单行输入框 7-明细 8-说明文字 9-金额 10- 图片 11-附件
     */
    private Integer type;
    /**
     * 枚举ID
     */
    private String enumId;
    /**
     * 枚举值ID
     */
    private String enumValueId;
    /**
     * 枚举值
     */
    private String enumValue;
    /**
     * 枚举显示值
     */
    private String enumName;
    /**
     * 枚举项列表
     */
    private List<LogEnumItemEntity> enumItems;

}
