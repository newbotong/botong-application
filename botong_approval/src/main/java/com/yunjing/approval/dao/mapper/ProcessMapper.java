package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.SetsProcess;
import org.apache.ibatis.annotations.Param;

/**
 * @author roc
 * @date 2017/12/21
 */
public interface ProcessMapper extends IBaseMapper<SetsProcess> {
    void deleteProcessUser(@Param("oid") Long oid, @Param("uid") Long uid);
}
