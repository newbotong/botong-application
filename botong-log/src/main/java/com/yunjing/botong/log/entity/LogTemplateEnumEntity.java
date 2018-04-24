package com.yunjing.botong.log.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

import java.util.List;

/**
 * @author 王开亮
 * @date 2018/4/2 11:46
 */
@TableName("log_template_enums")
@Data
public class LogTemplateEnumEntity extends BaseModel<LogTemplateEnumEntity>{

    /**
     * 枚举名
     */
    @TableField("enum_label")
    private String enumLabel;

    /**
     * 是否逻辑删除，1：删除，0：不删除
     */
    @TableField("deleted")
    private Boolean deleted;

    @TableField(exist=false)
    private List<LogTemplateEnumItemEntity> logTemplateEnumItemEntities;

}
