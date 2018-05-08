package com.yunjing.approval.dao.mapper;

import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.SetsCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
public interface ConditionMapper extends IBaseMapper<SetsCondition> {


    /**
     * 禁用审批条件
     *
     * @param modelId 模型主键
     * @return
     */
    void disableByModelId(@Param("modelId") String modelId);

    /**
     * 获取审批条件
     * @param modelId
     * @return
     */
    List<SetsCondition> selectConditionList(@Param("modelId") String modelId);
}
