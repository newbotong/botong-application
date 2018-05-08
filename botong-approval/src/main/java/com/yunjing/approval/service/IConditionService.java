package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.SetsCondition;
import com.yunjing.approval.model.vo.ConditionAndApproverVO;
import com.yunjing.approval.model.vo.ConditionVO;
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
    ConditionAndApproverVO getJudgeList(String modelId) throws Exception;

    /**
     * 保存审批条件
     *
     * @param modelId      模型编号
     * @param judge        选择的审批条件
     * @param memberIds    审批人
     * @param conditionIds 条件ids
     * @return
     * @throws Exception
     */
    List<SetConditionVO> save(String modelId, String judge, String memberIds, String conditionIds) throws Exception;

    /**
     * 删除审批条件
     *
     * @param modelId      模型主键
     * @param conditionIds 审批条件，以英文逗号隔开
     * @return
     * @throws Exception
     */
    boolean deleteProcess(String modelId, String conditionIds) throws Exception;

    /**
     * 获取审批条件
     *
     * @param modelId 模型主键
     * @return
     * @throws Exception
     */
    List<SetConditionVO> getConditionList(String modelId) throws Exception;

    /**
     * 获取审批条件
     *
     * @param modelId     模型主键
     * @param conditionVO 条件
     * @return
     */
    String getCondition(String modelId, ConditionVO conditionVO);

    /**
     * 获取条件
     *
     * @param modelId 模型主键
     * @return
     */
    List<SetsCondition> getFirstCondition(String modelId);
}
