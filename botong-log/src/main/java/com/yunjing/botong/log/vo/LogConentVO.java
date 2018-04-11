package com.yunjing.botong.log.vo;

import com.yunjing.botong.log.entity.LogEnumItemEntity;
import com.yunjing.botong.log.params.LogItemParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jingwj
 * @date 2018/3/30 17:06
 */
@Data
public class LogConentVO {
    /**
     * 日志模板字段Id
     */
    private String id;

    /**
     * 日志模板列名
     */
    private String name;

    /**
     * 显示值
     */
    private String value;

    /**
     * 日志模板列类型
     */
    private Integer type;

    /**
     * 唯一英文表示
     */
    private String key;

    /**
     * 枚举ID,单选和复选
     */
    private String enumId;

    /**
     * 枚举值ID,单选和复选
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

    public void fromParam(LogItemParam logItemParam, LogTemplateVo vo) {
        this.id= logItemParam.getFiledId();

        List<LogTemplateFieldVo> items = vo.getItems();

        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getId().equals(logItemParam.getFiledId())){
                this.key = items.get(i).getFieldName();
                this.name = items.get(i).getFieldLabel();
                this.value = logItemParam.getValue();
                this.type = items.get(i).getType();
                this.enumId = items.get(i).getEnumId();
                if(items.get(i).getType() == 3){
                    List<LogEnumItemEntity> logEnumItemEntities = new ArrayList<>();
                    for (int j = 0; j < items.get(i).getEnumItems().size(); j++) {
                        LogTemplateEnumItemVo enumitem = items.get(i).getEnumItems().get(j);
                        if(enumitem .getKey()==logItemParam.getValue()){
                            this.enumValueId=enumitem.getId();
                            this.enumValue=enumitem.getKey();
                            this.enumName=enumitem.getItemValue();
                        }
                        LogEnumItemEntity logEnumItemEntity = new LogEnumItemEntity(enumitem.getId(),enumitem.getKey(),enumitem.getItemValue());
                        logEnumItemEntities.add(logEnumItemEntity);
                    }
                    this.enumItems=logEnumItemEntities;
                }

            }
        }

    }

}
