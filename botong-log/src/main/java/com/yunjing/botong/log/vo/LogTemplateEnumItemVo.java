package com.yunjing.botong.log.vo;

import com.yunjing.botong.log.entity.LogTemplateEnumItemEntity;
import lombok.Data;

import javax.swing.text.html.parser.Entity;

/**
 * @author 王开亮
 * @date 2018/4/4 9:43
 */
@Data
public class LogTemplateEnumItemVo {

    /**
     * ID
     */
    private String id;

    /**
     * 枚举项值
     */
    private String key;

    /**
     * 枚举项显示名称
     */
    private String itemValue;

    public void fromEntity(LogTemplateEnumItemEntity entity) {
        this.id= entity.getId();
        this.key = entity.getItemKey();
        this.itemValue = entity.getItemValue();
    }
}
