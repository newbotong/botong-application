package com.yunjing.botong.log.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import com.yunjing.botong.log.params.LogTemplateParam;
import lombok.Data;

/**
 * @author auth
 * @date 2018/4/2 11:41
 */
@Data
@TableName("log_template_details")
public class LogTemplateFieldEntity extends BaseModel<LogTemplateFieldEntity >{

    /**
     *模板ID
     */
    @TableField("template_id")
    private String templateId ;

    /**
     *字段名
     */
    @TableField("field_name")
    private String fieldName ;

    /**
     *字段中文名
     */
    @TableField("field_label")
    private String fieldLabel ;

    /**
     *字段类型（1-多行输入框 2-数字输入框 3-单选框 4-日期 5-日期区间 6-单行输入框 7-明细 8-说明文字 9-金额 10- 图片 11-附件）
     */
    @TableField("field_type")
    private Integer fieldType ;

    /**
     *是否必填
     */
    @TableField("required")
    private Boolean required ;

    /**
     *默认值
     */
    @TableField("default_value")
    private String defaultValue ;

    /**
     *单位
     */
    @TableField("unit")
    private String unit  ;

    /**
     *枚举值ID
     */
    @TableField("enum_id")
    private String enumId;

    /**
     *帮助信息
     */
    @TableField("help")
    private String help  ;

    /**
     *排序
     */
    @TableField("sort")
    private Integer sort  ;

    /**
     *是否显示
     */
    @TableField("display")
    private Boolean display ;

    /**
     *是否最新版本
     */
    @TableField("currently")
    private Boolean currently ;

    /**
     *版本
     */
    @TableField("version")
    private Integer version ;

    /**
     *是否逻辑删除，1：删除，0：不删除
     */
    @TableField("deleted")
    private Boolean deleted ;

    @TableField(exist=false)
    private LogTemplateEnumEntity logTemplateEnumEntity ;

}
