package com.yunjing.approval.service;

import com.common.mybatis.page.Page;
import com.yunjing.approval.model.vo.ClientApprovalDetailVO;
import com.yunjing.approval.model.vo.ClientApprovalVO;
import com.yunjing.approval.model.vo.ClientModelVO;
import com.yunjing.approval.param.FilterParam;

import java.util.List;

/**
 * @author liuxiaopeng
 * @date 2018/02/28
 */
public interface IApprovalApiService {

    /**
     * 审批首页
     *
     * @param orgId 企业主键
     * @return
     */
    List<ClientModelVO> getList(Long orgId);

    /**
     * 获取待审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    Page<ClientApprovalVO> getWaited(Page page, Long orgId, Long userId, FilterParam filterParam);

    /**
     * 获取已审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    Page<ClientApprovalVO> getCompleted(Page page, Long orgId, Long userId, FilterParam filterParam);

    /**
     * 获取我发起的审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    Page<ClientApprovalVO> getLaunched(Page page, Long orgId, Long userId, FilterParam filterParam);

    /**
     * 获取抄送我的审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param orgId       企业主键
     * @param userId      用户主键
     * @param filterParam 搜索参数
     * @return
     */
    Page<ClientApprovalVO> getCopied(Page page, Long orgId, Long userId, FilterParam filterParam);

    /**
     * 获取审批详情
     *
     * @param orgId      企业主键
     * @param userId     用户主键
     * @param approvalId 审批主键
     * @return
     */
    ClientApprovalDetailVO getApprovalDetail(Long orgId, Long userId, Long approvalId);

    /**
     * 审批同意操作
     *
     * @param orgId      企业主键
     * @param userId     用户主键
     * @param approvalId 审批主键
     * @param state      审批状态
     * @return
     */
    boolean solveApproval(Long orgId, Long userId, Long approvalId, Integer state);

    /**
     * 审批撤销操作
     *
     * @param orgId      企业主键
     * @param userId     用户主键
     * @param approvalId 审批主键
     * @return
     * @throws Exception
     */
    boolean revokeApproval(Long orgId, Long userId, Long approvalId);

    /**
     * 审批转让操作
     *
     * @param orgId             企业主键
     * @param userId            用户主键
     * @param transferredUserId 被转让的审批人主键
     * @param approvalId        审批主键
     * @return
     * @throws Exception
     */
    boolean transferApproval(Long orgId, Long userId, Long transferredUserId, Long approvalId);

}
