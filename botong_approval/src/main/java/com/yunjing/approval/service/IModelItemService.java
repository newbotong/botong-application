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
     * @return 模型视图
     * @throws Exception 异常
     */
    ClientModelItemVO getModelItem(String modelId) throws Exception;

    /**
     * 保存模型
     *
     * @param orgId  企业主键
     * @param userId 用户主键
     * @param json   字段数据
     * @return 模型视图
     * @throws Exception 异常
     */
    ModelVO saveModelItem(String orgId, String userId, String json) throws Exception;
}
