package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.ModelItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型子项mapper
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
public interface ModelItemMapper extends IBaseMapper<ModelItem> {

    /**
     * 获取该企业所有模型子项
     *
     * @param orgId 企业主键
     * @return
     */
    List<ModelItem> selectAll(String orgId);


    /**
     * 删除企业下的所有审批模板子项
     * @param orgId 企业Id
     * @return
     */
    boolean deleteModelItemListByOrgId(@Param("orgId") String orgId);
}
