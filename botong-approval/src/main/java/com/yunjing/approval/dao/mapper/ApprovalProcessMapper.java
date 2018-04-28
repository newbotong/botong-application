package com.yunjing.approval.dao.mapper;

import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.approval.model.dto.ApprovalContentDTO;
import com.yunjing.approval.model.entity.ApprovalProcess;
import com.yunjing.approval.model.vo.ApprovalUserVO;
import com.yunjing.approval.param.FilterParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author roc
 * @date 2018/1/15
 */
public interface ApprovalProcessMapper extends IBaseMapper<ApprovalProcess> {

    /**
     * 分页查询-待我审批
     *
     * @param index       当前页
     * @param pageSize    每页显示条数
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    List<ApprovalContentDTO> getWaitedMeApprovalList(@Param("index") Integer index, @Param("size") Integer pageSize, @Param("orgId") String orgId,
                                                     @Param("userId") String userId, @Param("filterParam") FilterParam filterParam);
    /**
     * 查询-所有待我审批的数据
     *
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    List<ApprovalContentDTO> getAllWaitedMeApprovalList(@Param("orgId") String orgId,@Param("userId") String userId, @Param("filterParam") FilterParam filterParam);

    /**
     * 分页查询-已审批
     *
     * @param index       当前页
     * @param pageSize    每页显示条数
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    List<ApprovalContentDTO> getCompletedApprovalList(@Param("index") Integer index, @Param("size") Integer pageSize, @Param("orgId") String orgId,
                                                      @Param("userId") String userId, @Param("filterParam") FilterParam filterParam);

    /**
     * 分页查询-我发起的审批
     *
     * @param index       当前页
     * @param pageSize    每页显示条数
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    List<ApprovalContentDTO> getLaunchedApprovalList(@Param("index") Integer index, @Param("size") Integer pageSize, @Param("orgId") String orgId,
                                                     @Param("userId") String userId, @Param("filterParam") FilterParam filterParam);


    /**
     * 查询审批流程中信息和审批人信息
     *
     * @param approvalId 审批主键
     * @return
     */
    List<ApprovalUserVO> getApprovalUserList(String approvalId);
}
