package com.yunjing.botong.log.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yunjing.botong.log.entity.LogTemplateEntity;
import com.yunjing.botong.log.vo.LogTemplateItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 日志模板 Mapper
 * @author 王开亮
 * @date 2018/4/2 15:31
 */
public interface LogTemplateMapper extends BaseMapper<LogTemplateEntity>{

    /**
     * 跟据机构ID计算日志模板数量
     * @param orgId
     * @return
     */
    long totalLogTemplateByOrgId(@Param("orgId") long orgId);


    /**
     * 跟据机构ID分页查询日志模板列表
     * @param orgId
     * @param offset
     * @param pageSize
     * @return
     */
    List<LogTemplateItem> listLogTemplateByOrgId(@Param("orgId") long orgId,@Param("offset") int offset,@Param("pageSize") int pageSize);
}
