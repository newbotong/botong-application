package com.yunjing.approval.dao.mapper;

import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.entity.ApprovalProcess;
import com.yunjing.approval.model.vo.ApprovalContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author roc
 * @date 2018/1/15
 */
public interface ApprovalProcessMapper extends IBaseMapper<ApprovalProcess> {

    /**
     * 待我审批
     *
     * @param index     当前页
     * @param pageSize  每页显示条数
     * @param orgId     企业主键
     * @param userId    用户主键
     * @param searchKey 搜索关键字
     * @param flag      搜索关键字是不是日期
     * @return
     */
    List<ApprovalContent> getMyApprovalList(@Param("index") Integer index, @Param("size") Integer pageSize, @Param("orgId") Long orgId,
                                            @Param("userId") Long userId, @Param("searchKey") String searchKey, @Param("flag") Boolean flag);
}
