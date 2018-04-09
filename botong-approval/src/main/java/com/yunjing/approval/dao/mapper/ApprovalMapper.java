package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.dto.ApprovalDetailDTO;
import com.yunjing.approval.model.entity.Approval;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 审批mapper
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
public interface ApprovalMapper extends IBaseMapper<Approval> {

    /**
     * 查询审批详情
     *
     * @param approvalId 审批主键
     * @return
     */
    List<ApprovalDetailDTO> getApprovalDetail(String approvalId);

    /**
     * 查询审批信息
     *
     * @param approvalId 审批主键
     * @return
     */
    ApprovalDetailDTO getApprovalById(@Param("approvalId") String approvalId);

}
