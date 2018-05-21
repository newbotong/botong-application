package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.ModelItem;
import com.yunjing.approval.model.vo.ApproverVO;
import com.yunjing.approval.model.vo.ClientModelItemVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.model.vo.UserVO;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
public interface IModelItemService extends IBaseService<ModelItem> {

    /**
     * 获取模型
     *
     * @param companyId 公司id
     * @param modelId   模型主键
     * @param memberId  成员id
     * @return 模型视图
     * @throws Exception 异常
     */
    ClientModelItemVO getModelItem(String companyId, String modelId, String memberId) throws Exception;

    /**
     * 获取模型
     *
     * @param modelId 模型主键
     * @return 模型视图
     * @throws Exception 异常
     */
    ModelVO get(String modelId) throws Exception;

    /**
     * 保存模型
     *
     * @param companyId  公司id
     * @param memberId   成员id
     * @param categoryId 分组id
     * @param json       字段数据
     * @return 模型视图
     * @throws Exception 异常
     */
    ModelVO saveModelItem(String companyId, String memberId, String categoryId, String json) throws Exception;

    /**
     * 删除企业下的模板子项
     *
     * @param orgId 企业主键
     * @return
     */
    boolean deleteModelItemListByOrgId(String orgId);

    /**
     * 客户端-获取默认审批人和抄送人
     *
     * @param companyId 公司id
     * @param modelId   模型id
     * @param memberId  成员id
     * @return ApproverVO
     */
    ApproverVO getDefaultApproverAndCopy(String companyId, String modelId, String memberId);

    /**
     * 客户端-获取默认审批人
     *
     * @param companyId    公司id
     * @param memberId     成员id
     * @param modelId      模型id
     * @param deptId       部门id
     * @return List<UserVO>
     */
    ApproverVO getDefaultProcess(String companyId, String memberId, String modelId, String deptId);
}
