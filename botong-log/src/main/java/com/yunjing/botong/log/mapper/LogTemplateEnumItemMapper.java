package com.yunjing.botong.log.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yunjing.botong.log.entity.LogTemplateEnumItemEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 枚举项 Mapper
 *
 * @author 王开亮
 * @date 2018/4/2 15:33
 */
public interface LogTemplateEnumItemMapper extends BaseMapper<LogTemplateEnumItemEntity> {

    /**
     * 批量插入枚举项
     * @param enumItems
     */
    void batchInsertLogTemplateEnumItems(@Param("enumItems") List<LogTemplateEnumItemEntity> enumItems);

}
