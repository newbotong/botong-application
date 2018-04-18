package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.ModelItem;
import com.yunjing.approval.model.vo.ClientModelItemVO;
import com.yunjing.approval.model.vo.ModelVO;

/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
public interface IModelItemService extends IBaseService<ModelItem> {

    /**
     * 获取模型
     *
     * @param modelId 模型主键
     * @param memberId 成员id
     * @return 模型视图
     * @throws Exception 异常
     */
    ClientModelItemVO getModelItem(String modelId,String memberId) throws Exception;

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
}
