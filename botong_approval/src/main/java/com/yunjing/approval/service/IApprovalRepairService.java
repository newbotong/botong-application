package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.Approval;

import java.util.List;

/**
 * @author roc
 * @date 2018/1/15
 */
public interface IApprovalRepairService extends IBaseService<Approval> {

    /**
     * 修复审批标题数据
     *
     * @param oid 企业主键
     * @return 审批列表
     * @throws Exception 异常
     */
    List<Approval> repairTitle(Long oid) throws Exception;

    /**
     * 修复审批完成时间数据
     *
     * @param oid 企业主键
     * @return 审批列表
     * @throws Exception 异常
     */
    List<Approval> repairFinishTime(Long oid);
}
