package com.yunjing.approval.service;

import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.Approval;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/1/15
 */
public interface IApprovalRepairService extends IBaseService<Approval> {

    /**
     * 修复审批标题数据
     *
     * @param companyId 公司id
     * @return 审批列表
     */
    List<Approval> repairTitle(String companyId);

    /**
     * 修复审批完成时间数据
     *
     * @param companyId 公司id
     * @return 审批列表
     */
    List<Approval> repairFinishTime(String companyId);
}
