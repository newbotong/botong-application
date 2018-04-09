package com.yunjing.approval.model.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型下的具体项目
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
@TableName("model_item")
@EqualsAndHashCode(callSuper = true)
public class ModelItem extends BaseModel<ModelItem> {

    /**
     * 模型主键
     */
    @TableField("model_id")
    private String modelId;

    /**
     * 字段
     */
    @TableField("field")
    private String field;

    /**
     * 名称
     */
    @TableField("item_label")
    private String itemLabel;
    /**
     * 第二名称
     */
    @TableField("item_labels")
    private String itemLabels;

    /**
     * 排序顺序
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 默认值
     */
    @TableField("def_value")
    private String defValue;

    /**
     * 可选项
     */
    @TableField("opt_value")
    private String optValue;

    /**
     * 提示文字
     */
    @TableField("help")
    private String help;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 日期格式
     */
    @TableField("date_format")
    private String dateFormat;

    /**
     * 1:多行输入框 2:数字输入框 3:单选框 4:日期 5:日期区间 6:单行输入框 7:明细 8:说明 9:金额 11:附件 12:图片
     */
    @TableField("data_type")
    private Integer dataType;

    /**
     * 是否自定义
     */
    @TableField("is_custom")
    private Integer isCustom;

    /**
     * 是否显示 0:不显示 1:显示
     */
    @TableField("is_display")
    private Integer isDisplay;

    /**
     * 是否必填
     */
    @TableField("is_required")
    private Integer isRequired;

    /**
     * 是否为判断条件
     */
    @TableField("is_judge")
    private Integer isJudge;

    /**
     * 是否为子项
     */
    @TableField("is_child")
    private String    isChild;

    /**
     * 版本
     */
    @TableField("item_version")
    private Integer itemVersion;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}