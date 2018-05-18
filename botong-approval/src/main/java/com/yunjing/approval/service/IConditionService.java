package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.SetsCondition;
import com.yunjing.approval.model.vo.ConditionAndApproverVO;
import com.yunjing.approval.model.vo.ConditionVO;
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
     * @return List<ModelItemVO>
     */
    List<ModelItemVO> getJudgeList(String modelId);

    /**
     * 编辑某一条时--获取审批条件及对应审批人
     *
     * @param modelId     模型主键
     * @param conditionId 条件主键
     * @return ConditionAndApproverVO
     */
    ConditionAndApproverVO getConditionAndApprover(String modelId, String conditionId);

    /**
     * 获取审批条件及对应审批人的列表
     *
     * @param modelId 模型编号
     * @return List<ConditionAndApproverVO>
     */
    List<ConditionAndApproverVO> getConditionAndApproverList(String modelId);

    /**
     * 审批条件列表优先级排序
     *
     * @param modelId   模型主键
     * @param sortArray 序号数组([{"conditionId":"sort"},])
     * @return boolean
     */
    boolean sortedCondition(String modelId, String sortArray);

    /**
     * 保存审批条件
     *
     * @param modelId     模型编号
     * @param judge       选择的审批条件
     * @param memberIds   审批人
     * @param conditionId 条件ids
     * @return List<SetConditionVO>
     */
    List<SetConditionVO> saveSetsCondition(String modelId, String judge, String memberIds, String conditionId);

    /**
     * 删除审批条件
     *
     * @param modelId      模型主键
     * @param conditionIds 审批条件，以英文逗号隔开
     * @return boolean
     */
    boolean deleteProcess(String modelId, String conditionIds);

    /**
     * 获取审批条件
     *
     * @param modelId 模型主键
     * @return List<SetConditionVO>
     */
    List<SetConditionVO> getConditionList(String modelId);

    /**
     * 获取审批条件
     *
     * @param modelId      模型主键
     * @param conditionVOS 条件
     * @return String
     */
    String getCondition(String modelId, List<ConditionVO> conditionVOS);

}
