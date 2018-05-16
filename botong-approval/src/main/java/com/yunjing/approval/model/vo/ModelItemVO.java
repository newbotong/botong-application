package com.yunjing.approval.model.vo;

import com.yunjing.approval.model.entity.ModelItem;
import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/12/22
 */
@Data
public class ModelItemVO {
    public ModelItemVO() {

    }

    public ModelItemVO(ModelItem item) {
        this.type = item.getDataType();
        this.field = item.getField();
        this.label = item.getItemLabel();
        this.labels = item.getItemLabels();
        this.option = item.getOptValue();
        this.priority = item.getPriority();
        this.help = item.getHelp();
        this.unit = item.getUnit();
        this.format = item.getDateFormat();
        this.display = item.getIsDisplay();
        this.required = item.getIsRequired();
        this.judge = item.getIsJudge();
        this.itemVersion = item.getItemVersion();
        this.modelItemId = item.getId();
    }

    private String modelId;
    /**
     * 字段类型     1:多行输入框 2:数字输入框 3:单选框 4:日期 5:日期区间 6:单行输入框 7:明细 8:说明 9:金额 11:附件 12:图片
     */
    private Integer type;

    /**
     * 字段
     */
    private String field;

    /**
     * 单选框选中的值
     */
    private String value;

    /**
     * 数字类型设置的值
     */
    private String dayNum;
    /**
     * 显示名称
     */
    private String label;
    /**
     * 显示名称
     */
    private String labels;

    /**
     * 可选项
     */
    private String option;

    /**
     * 排序
     */
    private Integer priority;

    /**
     * 帮助信息
     */
    private String help;

    /**
     * 单位
     */
    private String unit;

    /**
     * 日期格式
     */
    private String format;

    /**
     * 是否显示
     */
    private Integer display;

    /**
     * 是否必填
     */
    private Integer required;

    /**
     * 是否为审批条件
     */
    private Integer judge;

    /**
     * 模型项主键
     */
    private String modelItemId;

    /**
     * 模型项版本
     */
    private Integer itemVersion;

    /**
     * 子项
     */
    private List<ModelItemVO> child;
    /**
     * 子项
     */
    private List<ModelItem> items;
}
