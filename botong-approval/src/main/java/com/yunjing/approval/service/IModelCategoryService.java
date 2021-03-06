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
     * @param companyId    公司id
     * @param categoryId   分组主键
     * @param categoryName 分组名称
     * @return boolean
     */
    boolean createOrEditCategory(String companyId, String categoryId, String categoryName);

    /**
     * 删除分组
     *
     * @param companyId  公司id
     * @param categoryId 分组主键
     * @return boolean
     */
    boolean deleteCategory(String companyId, String categoryId);

    /**
     * 分组排序
     *
     * @param companyId 公司id
     * @param sortArray 序号数组
     * @return boolean
     * @throws Exception
     */
    boolean sortedCategory(String companyId, String sortArray) throws Exception;

    /**
     * 查询分组列表
     *
     * @param companyId 公司id
     * @return  List<ModelCategoryVO>
     */
    List<ModelCategoryVO> getCategoryList(String companyId);

    /**
     * 给旧数据初始化分组信息并更新model表分组字段信息
     * @param companyId 公司id
     * @return
     */
    boolean initModelCategory(String companyId);
}
