package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.SetsCondition;
import com.yunjing.approval.model.vo.ModelItemVO;
import com.yunjing.approval.model.vo.SetConditionVO;

import java.util.List;

/**
 * @author 刘小鹏
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
     * @param judge   选择的审批条件
     * @param memberIds 审批人
     * @return
     * @throws Exception
     */
    List<SetConditionVO> save(String modelId, String judge,String memberIds) throws Exception;

    /**
     * 删除审批条件
     *
     * @param modelId    模型编号
     * @return
     * @throws Exception
     */
    boolean delete(String modelId) throws Exception;

    /**
     * 获取审批条件
     *
     * @param modelId 模型编号
     * @return
     * @throws Exception
     */
    List<SetConditionVO> getConditionList(String modelId) throws Exception;

    /**
     * 获取审批条件
     *
     * @param modelId 模型主键
     * @param value   条件的值
     * @return
     */
    String getCondition(String modelId, String value);

    /**
     * 获取条件
     *
     * @param modelId 模型主键
     * @return
     */
    List<SetsCondition> getFirstCondition(String modelId);
}
