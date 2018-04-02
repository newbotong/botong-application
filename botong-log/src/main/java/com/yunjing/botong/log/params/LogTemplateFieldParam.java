package com.yunjing.botong.log.params;

import lombok.Data;

import java.util.List;

/**
 * 日志模板字段定义
 *
 * @author 王开亮
 * @date 2018/3/30 16:21
 */
@Data
public class LogTemplateFieldParam {
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 字段中文名
     */
    private String fieldLabel;
    /**
     * 字段类型 1-多行输入框 2-数字输入框 3-单选框 4-日期 5-日期区间 6-单行输入框 7-明细 8-说明文字 9-金额 10- 图片 11-附件）
     */
    private Integer fieldType;
    /**
     * 是否必填
     */
    private Boolean required;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 单位
     */
    private String unit;
    /**
     * 帮助信息
     */
    private String help;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 是否显示
     */
    private Boolean display;
    /**
     * 是否显示
     */
    private List<EnumItemParam> enumItems;

}
