package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.ApprovalSets;
import com.yunjing.approval.model.vo.ApprovalSetVO;

/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
public interface IApprovalSetsService extends IBaseService<ApprovalSets> {

    /**
     * 获取审批设置信息
     *
     * @param modelId 模型主键
     * @return
     * @throws Exception
     */
    ApprovalSetVO getApprovalSet(Long modelId) throws Exception;

    /**
     * 保存审批设置信息
     *
     * @param modelId 模型主键
     * @param setting 设置类型 0:不分条件设置审批人 1:分条件设置审批人
     * @return
     * @throws Exception
     */
    boolean saveApprovalSets(Long modelId, Integer setting) throws Exception;

}
