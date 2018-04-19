package com.yunjing.approval.model.dto;


import lombok.Data;

/**
 * 模型下的具体项目
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
public class ModelItemDTO {

    /**
     * 主键
     */
    private String modelItemId;

    /**
     * 模型主键
     */
    private String modelId;

    /**
     * 字段
     */
    private String field;

    /**
     * 名称
     */
    private String itemLabel;

    /**
     * 排序顺序
     */
    private Integer priority;

    /**
     * 默认值
     */
    private String defValue;

    /**
     * 可选项
     */
    private String optValue;

    /**
     * 提示文字
     */
    private String help;

    /**
     * 单位
     */
    private String unit;

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 1:多行输入框 2:数字输入框 3:单选框 4:日期 5:日期区间 6:单行输入框 7:明细 8:说明 9:金额 11:附件 12:图片
     */
    private Integer dataType;

    /**
     * 是否自定义
     */
    private Integer isCustom;

    /**
     * 是否显示 0:不显示 1:显示
     */
    private Integer isDisplay;

    /**
     * 是否必填
     */
    private Integer isRequired;

    /**
     * 是否为判断条件
     */
    private Integer isJudge;

    /**
     * 是否为子项
     */
    private String isChild;

    /**
     * 版本
     */
    private Integer itemVersion;


}