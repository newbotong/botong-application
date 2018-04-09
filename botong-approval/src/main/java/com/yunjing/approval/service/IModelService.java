package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.ModelListVO;
import com.yunjing.approval.model.vo.ModelVO;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
public interface IModelService extends IBaseService<ModelL> {

    /**
     * 查询企业下的初始化模板
     *
     * @param companyId 公司id
     * @return
     */
    List<ModelListVO> findModelList(String companyId);

    /**
     * 审批模板排序
     *
     * @param categoryId 分组主键
     * @param sortArray  序号数组
     * @return
     * @throws Exception 异常
     */
    boolean sortedModel(String categoryId, String sortArray) throws Exception;

    /**
     * 移动审批模板
     *
     * @param categoryId 要移动到的分组主键
     * @param modelId    模型主键
     * @return
     * @throws Exception 异常
     */
    boolean moveModel(String categoryId, String modelId) throws Exception;

    /**
     * 查询企业下的初始化模板
     *
     * @param companyId 公司id
     * @return
     */
    List<ModelVO> findModelListByOrgId(String companyId);

    /**
     * 设置是否停用模型
     *
     * @param modelId    模型主键
     * @param  deptIds（多个部门以英文,隔开）
     * @param  userIds（多个用户以英文,隔开）
     * @return
     * @throws Exception
     */
    boolean updateVisibleRange(String modelId, String deptIds, String userIds) throws Exception;

    /**
     * 修改可见范围
     *
     * @param modelId    模型主键
     * @param isDisabled 是否禁用（0：启用，1,：停用）
     * @return
     * @throws Exception
     */
    boolean updateIsDisabled(String modelId, Integer isDisabled) throws Exception;

    /**
     * 获取系统模板logo
     *
     * @return
     * @throws Exception
     */
    List<String> getLogo() throws Exception;


}
