package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.Copy;
import org.apache.ibatis.annotations.Param;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
public interface CopyMapper extends IBaseMapper<Copy> {

    /**
     * 删除抄送人
     *
     * @param oid 企业主键
     * @param uid 用户主键
     * @return boolean
     */
    boolean deleteCopyUser(@Param("oid") Long oid, @Param("uid") Long uid);
}
