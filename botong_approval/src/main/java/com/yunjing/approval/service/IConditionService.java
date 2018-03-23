package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.SetsCondition;
import com.yunjing.approval.model.vo.ModelItemVO;
import com.yunjing.approval.model.vo.SetConditionVO;

import java.util.List;

/**
 * @author roc
 * @date 2017/12/21
 */
public interface IConditionService extends IBaseService<SetsCondition> {

    /**
     * 获取模型条件
     *
     * @param modelId 模型编号
     * @return
     * @throws Exception
     */
    List<ModelItemVO> getJudgeList(String modelId) throws Exception;

    /**
     * 保存审批条件
     *
     * @param modelId 模型编号
     * @param field 字段
     * @param numbers 天数
     * @return
     * @throws Exception
     */
    List<SetConditionVO> save(String modelId, String field, String numbers) throws Exception;

    /**
     * 删除审批条件
     *
     * @param modelId 模型编号
     * @param conditions 审批条件编号
     * @return
     * @throws Exception
     */
    boolean delete(String modelId, String conditions) throws Exception;

    /**
     * 获取审批条件
     *
     * @param modelId 模型编号
     * @return
     * @throws Exception
     */
    List<SetConditionVO> getConditionList(String modelId) throws Exception;
}