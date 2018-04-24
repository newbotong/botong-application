package com.yunjing.botong.log.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

/**
 * @author 王开亮
 * @date 2018/4/2 11:51
 */
@TableName("log_template_enum_items")
@Data
public class LogTemplateEnumItemEntity extends BaseModel<LogTemplateEnumItemEntity>{
    /**
     * 名称
     */
    @TableField("item_key")
    private  String itemKey;

    /**
     * 值
     */
    @TableField("item_value")
    private  String itemValue;

    /**
     *枚举值ID
     */
    @TableField("enum_id")
    private String enumId;

    /**
     * 排序
     */
    @TableField("sort")
    private  Integer sort;

    /**
     * 是否逻辑删除，1：删除，0：不删除
     */
    @TableField("deleted")
    private  Boolean deleted;
}
