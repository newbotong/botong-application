package com.yunjing.botong.log.params;

import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.yunjing.botong.log.entity.LogTemplateEnumEntity;
import com.yunjing.botong.log.entity.LogTemplateEnumItemEntity;
import com.yunjing.botong.log.entity.LogTemplateFieldEntity;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 日志模板参数
 *
 * @author 王开亮
 * @date 2018/3/30 16:17
 */
@Data
public class LogTemplateParam {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板图片
     */
    private String iconImage;
    /**
     * 所属机构ID
     */
    private String orgId;
    /**
     * 提交周期（1每天 2 每周 3 每月 4 季度 5 年度）
     */
    private int submitType;
    /**
     * 排序序号
     */
    private int sort;
    /**
     * 模板字段明细
     */
    private List<LogTemplateFieldParam> details;


    public List<LogTemplateFieldEntity> getLogTemplateFieldEntity(){

        if(CollectionUtils.isEmpty(this.details)){
            return Collections.emptyList();
        }
        List<LogTemplateFieldEntity> result = new ArrayList<LogTemplateFieldEntity>();
        for (int i = 0; i < this.details.size(); i++) {
            LogTemplateFieldEntity it = new LogTemplateFieldEntity();
            LogTemplateFieldParam par = this.details.get(i);
            it.setId(IdWorker.get32UUID());
            it.setFieldName(par.getFieldName());
            it.setFieldLabel(par.getFieldLabel());
            it.setFieldType(par.getFieldType());
            it.setRequired(par.getRequired());
            it.setDefaultValue(par.getDefaultValue());
            it.setUnit(par.getUnit());
            it.setHelp(par.getHelp());
            it.setSort(par.getSort());
            it.setDisplay(par.getDisplay());
            it.setCurrently(true);
            it.setVersion(1);
            it.setDeleted(false);
            it.setCreateTime(System.currentTimeMillis());
            it.setUpdateTime(System.currentTimeMillis());
            it.setDeleted(false);
            if(par.getFieldType() == 3){
                LogTemplateEnumEntity enumEntity = new LogTemplateEnumEntity();
                enumEntity.setId(IdWorker.get32UUID());
                enumEntity.setEnumLabel(par.getFieldName()+"_enum");
                enumEntity.setCreateTime(System.currentTimeMillis());
                enumEntity.setUpdateTime(System.currentTimeMillis());
                enumEntity.setDeleted(false);
                List<EnumItemParam> enumItemPars = par.getEnumItems();
                List<LogTemplateEnumItemEntity> enumItems = new ArrayList<>();
                for (int j = 0; j < enumItemPars.size(); j++) {
                    LogTemplateEnumItemEntity itemEntity = new LogTemplateEnumItemEntity();
                    itemEntity.setId(IdWorker.get32UUID());
                    itemEntity.setItemKey(enumItemPars.get(j).getName());
                    itemEntity.setItemValue(enumItemPars.get(j).getValue());
                    itemEntity.setEnumId(enumEntity.getId());
                    itemEntity.setSort(j+1);
                    itemEntity.setCreateTime(System.currentTimeMillis());
                    itemEntity.setUpdateTime(System.currentTimeMillis());
                    itemEntity.setDeleted(false);
                    enumItems.add(itemEntity);
                }
                enumEntity.setLogTemplateEnumItemEntities(enumItems);
                it.setLogTemplateEnumEntity(enumEntity);
                it.setEnumId(enumEntity.getId());
            }
            result.add(it);
        }
        return result;
    }


}
