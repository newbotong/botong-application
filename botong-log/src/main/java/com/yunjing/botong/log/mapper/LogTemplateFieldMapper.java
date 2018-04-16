package com.yunjing.botong.log.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yunjing.botong.log.entity.LogTemplateFieldEntity;
import com.yunjing.botong.log.vo.LogTemplateFieldVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 模板字段定义 Mapper
 *
 * @author 王开亮
 * @date 2018/4/2 15:32
 */
@Repository
public interface LogTemplateFieldMapper extends BaseMapper<LogTemplateFieldEntity> {

    /**
     * 批量插入模板字段
     *
     * @param logTemplateFieldEntities
     */
    void batchInsertLogTemplateFields(@Param("logTemplateFieldEntities") List<LogTemplateFieldEntity> logTemplateFieldEntities);

    /**
     * 批量跟据template Id更新Currently
     *
     * @param templateId
     * @param currently
     */
    void updateCurrentlyByLogTemplateId(@Param("templateId") String templateId, @Param("currently") boolean currently);

    /**
     * 查询所有的列头
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param type      类别
     * @param last      是否最新
     * @return 集合
     */
    List<LogTemplateFieldVo> queryFields(@Param("startDate") Long startDate, @Param("endDate") Long endDate, @Param("type") String type, @Param("last") String last);
}
