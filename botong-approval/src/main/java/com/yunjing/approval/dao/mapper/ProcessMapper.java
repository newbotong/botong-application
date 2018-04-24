package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.SetsProcess;
import org.apache.ibatis.annotations.Param;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
public interface ProcessMapper extends IBaseMapper<SetsProcess> {

    /**
     * 删除审批人
     *
     * @param companyId 企业主键
     * @param memberId 用户主键
     */
    void deleteProcessUser(@Param("companyId") String companyId, @Param("memberId") String memberId);
}
