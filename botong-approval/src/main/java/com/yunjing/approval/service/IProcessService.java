package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.SetsProcess;
import com.yunjing.approval.model.vo.ApproverVO;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.mommon.global.exception.BaseException;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
public interface IProcessService extends IBaseService<SetsProcess> {

    /**
     * 清空审批流程
     *
     * @param modelId     模型编号
     * @param conditionId 审批条件主键
     * @return
     * @throws Exception
     */
    boolean delete(String modelId, String conditionId);

    /**
     * 获取审批流程
     *
     * @param modelId      模型主键
     * @param conditionIds 条件主键
     * @return
     * @throws Exception
     */
    List<UserVO> getProcess(String modelId, List<String> conditionIds);

    /**
     * 设置审批流程信息
     *
     * @param modelId     模型主键
     * @param conditionId 条件主键
     * @param userArray   用户集合
     * @return
     * @throws Exception
     */
    boolean updateProcess(String modelId, String conditionId, String userArray);

    /**
     * 获取审批人
     *
     * @param companyId   公司主键
     * @param memberId    成员主键
     * @param modelId     模型主键
     * @param deptId      部门主键
     * @param judge       条件的值
     * @return ApproverVO
     * @throws Exception
     */
    ApproverVO getApprover(String companyId, String memberId, String modelId, String deptId, String judge);

    /**
     * 管理端--保存默认审批人和抄送人
     *
     * @param modelId     模型Id
     * @param approverIds 审批人ID集合
     * @param copyIds     抄送人ID集合
     * @return
     * @throws BaseException
     */
    boolean saveDefaultApprover(String modelId, String approverIds, String copyIds);

    /**
     * 管理端--获取默认审批人和抄送人
     *
     * @param modelId     模型Id
     * @return
     * @throws BaseException
     */
    ApproverVO getDefaultApprover(String modelId);
}
