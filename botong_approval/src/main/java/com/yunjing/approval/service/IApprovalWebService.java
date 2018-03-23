package com.yunjing.approval.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.approval.model.vo.ApprovalContent;
import com.yunjing.approval.model.vo.ClientModelVO;

import java.util.List;

/**
 * @author liuxiaopeng
 * @date 2018/02/28
 */
public interface IApprovalWebService {

    /**
     * 审批首页
     *
     * @param orgId 企业主键
     * @return
     */
    List<ClientModelVO> getList(String orgId);

    /**
     * 获取待审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param orgId     企业主键
     * @param userId    用户主键
     * @param state     审批状态
     * @param searchKey 搜索标题
     * @return
     */
    Page<ApprovalContent> getMyApprovalList(Page page, String orgId, String userId, Integer state, String searchKey);
}
