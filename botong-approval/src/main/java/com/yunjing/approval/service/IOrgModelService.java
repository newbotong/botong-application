package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.OrgModel;

/**
 * 企业模型接口
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
public interface IOrgModelService extends IBaseService<OrgModel> {

    /**
     * 初始化企业审批模板
     *
     * @param orgId 企业主键
     * @return
     * @throws Exception
     */
    boolean createApprovalModel(String orgId);

    /**
     * 删除企业审批模板
     *
     * @param orgId 企业主键
     * @return
     * @throws Exception
     */
    boolean deleteApprovalModel(String orgId);

}
