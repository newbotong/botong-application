package com.yunjing.botong.log.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yunjing.botong.log.entity.LogTemplateFieldEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板字段定义 Mapper
 * @author 王开亮
 * @date 2018/4/2 15:32
 */
public interface LogTemplateFieldMapper extends BaseMapper<LogTemplateFieldEntity> {

    /**
     * 批量插入模板字段
     *
     * @param logTemplateFieldEntities
     */
    void batchInsertLogTemplateFields(@Param("logTemplateFieldEntities") List<LogTemplateFieldEntity> logTemplateFieldEntities);

}
