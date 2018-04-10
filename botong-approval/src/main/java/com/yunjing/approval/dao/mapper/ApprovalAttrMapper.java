package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.ApprovalAttr;
import com.yunjing.approval.model.vo.ApproveAttributeVO;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/01/15
 */
public interface ApprovalAttrMapper extends IBaseMapper<ApprovalAttr> {

    /**
     * 查询企业下审批数据的详细信息
     *
     * @param orgId 企业主键
     * @return
     */
    List<ApprovalAttr> selectAttrByOrgId(String orgId);

    /**
     * 查询企业下审批数据的详细信息
     *
     * @param approvalId 审批主键
     * @return
     */
    List<ApproveAttributeVO> selectAttrList(String approvalId);

    /**
     * 查询企业下审批数据的详细信息
     *
     * @param orgId 企业主键
     * @return
     */
    List<ApproveAttributeVO> selectAttrListByOrgId(String orgId);

}
