package com.yunjing.botong.log.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yunjing.botong.log.entity.LogTemplateEnumEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 枚举定义Mapper
 *
 * @author 王开亮
 * @date 2018/4/2 15:34
 */
public interface LogTemplateEnumMapper extends BaseMapper<LogTemplateEnumEntity>{

    /**
     * 批量创建枚举定义
     * @param enums
     */
    void batchInsertLogTemplateEnums(@Param("enums") List<LogTemplateEnumEntity> enums);

}
