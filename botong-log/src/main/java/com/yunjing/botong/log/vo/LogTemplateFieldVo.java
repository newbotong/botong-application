package com.yunjing.botong.log.vo;

import com.yunjing.botong.log.entity.LogTemplateFieldEntity;
import lombok.Data;

import java.util.List;

/**
 * @author auth
 * @date 2018/4/4 9:38
 */
@Data
public class LogTemplateFieldVo {


    /**
     * ID
     */
    private Long id;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段显示名
     */
    private String fieldLabel;

    /**
     * 类型1-多行输入框 2-数字输入框 3-单选框 4-日期 5-日期区间 6-单行输入框 7-明细 8-说明文字 9-金额 10- 图片 11-附件
     */
    private Integer type;

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
     * 提示信息
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
     * 版本
     */
    private Integer version;

    /**
     * 枚举ID
     */
    private Long enumId;

    /**
     * 枚举项列表
     */
    private List<LogTemplateEnumItemVo> enumItems;

    public void fromEntity(LogTemplateFieldEntity entity) {
        this.id = entity.getId();
        this.fieldName = entity.getFieldName();
        this.fieldLabel = entity.getFieldLabel();
        this.type = entity.getFieldType();
        this.required = entity.getRequired();
        this.defaultValue = entity.getDefaultValue();
        this.unit = entity.getUnit();
        this.help = entity.getHelp();
        this.sort = entity.getSort();
        this.display = entity.getDisplay();
        this.version = entity.getVersion();
        this.enumId = entity.getEnumId();
    }
}
