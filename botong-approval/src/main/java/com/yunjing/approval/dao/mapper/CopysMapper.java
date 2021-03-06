package com.yunjing.approval.dao.mapper;


import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.dto.ApprovalContentDTO;
import com.yunjing.approval.model.entity.Copys;
import com.yunjing.approval.model.vo.CopyUserVO;
import com.yunjing.approval.param.FilterParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/01/15
 */
public interface CopysMapper extends IBaseMapper<Copys> {


    /**
     * 查询-抄送我的审批
     *
     * @param index       当前页
     * @param pageSize    每页显示条数
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    List<ApprovalContentDTO> getCopiedApprovalList(@Param("index") Integer index, @Param("size") Integer pageSize, @Param("orgId") String orgId,
                                                   @Param("userId") String userId, @Param("filterParam") FilterParam filterParam);

    /**
     * 查询抄送人列表
     *
     * @param approvalId 审批主键
     * @return
     */
    List<CopyUserVO> getCopyUserList(String approvalId);
}
