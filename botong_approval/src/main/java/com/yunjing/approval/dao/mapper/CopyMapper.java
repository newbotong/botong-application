package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.Copy;
import org.apache.ibatis.annotations.Param;

/**
 * @author roc
 * @date 2017/12/21
 */
public interface CopyMapper extends IBaseMapper<Copy> {

    void deleteCopyUser(@Param("oid") String oid, @Param("uid") String uid);
}
