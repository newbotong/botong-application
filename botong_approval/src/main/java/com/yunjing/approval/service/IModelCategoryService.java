package com.yunjing.approval.service;

import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.ModelCategory;
import com.yunjing.approval.model.vo.ModelCategoryVO;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
public interface IModelCategoryService extends IBaseService<ModelCategory> {

    /**
     * 新增分组
     *
     * @param orgId        企业主键
     * @param categoryId   分组主键
     * @param categoryName 分组名称
     * @return
     */
    boolean createOrEditCategory(String orgId, Long categoryId, String categoryName);

    /**
     * 删除分组
     *
     * @param orgId      企业主键
     * @param categoryId 分组主键
     * @return
     */
    boolean deleteCategory(String orgId, Long categoryId);

    /**
     * 分组排序
     *
     * @param orgId     企业主键
     * @param sortArray 序号数组
     * @return
     */
    boolean sortedCategory(String orgId, String sortArray) throws Exception;

    /**
     * 查询分组列表
     *
     * @param orgId 企业主键
     * @return
     */
    List<ModelCategoryVO> getCategoryList(String orgId);
}
