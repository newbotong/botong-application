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
public interface ModelMapper extends IBaseMapper<ModelL> {

    /**
     * 为客户端查询企业下的所有审批模板
     * @param orgId 企业Id
     * @return List<ModelVO>
     */
    List<ModelVO> selectModelList(@Param("orgId") String orgId);

    /**
     * 获取审批模板最大排序值
     * @param orgId 企业Id
     * @return 最大值
     */
    Integer getMaxSort(@Param("orgId") String orgId);

    /**
     * 为管理端查询企业下的所有审批模板
     * @param orgId 企业Id
     * @return
     */
    List<ModelVO> selectLists(@Param("orgId") String orgId);

    /**
     * 查询企业下的所有审批模板
     * @param orgId 企业Id
     * @return
     */
    List<ModelL> selectModelListByOrgId(@Param("orgId") String orgId);
    /**
     * 删除企业下的所有审批模板
     * @param orgId 企业Id
     * @return
     */
    boolean deleteModelListByOrgId(@Param("orgId") String orgId);
}
