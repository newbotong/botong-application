package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.ModelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型mapper
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
public interface ModelLMapper extends IBaseMapper<ModelL> {

    /**
     * 查询企业下的所有审批模板
     * @param orgId 企业Id
     * @return
     */
    List<ModelVO> selectModelListByOrgId(@Param("orgId") Long orgId);

    /**
     * 获取审批模板最大排序值
     * @param orgId 企业Id
     * @return 最大值
     */
    Integer getMaxSort(@Param("orgId") Long orgId);
}
