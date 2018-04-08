package com.yunjing.approval.service;


import com.baomidou.mybatisplus.plugins.Page;
import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.excel.BaseExModel;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.model.vo.ApprovalPageVO;

/**
 * @author 刘小鹏
 * @date 2018/1/15
 */
public interface IApprovalService extends IBaseService<Approval> {

    /**
     * 提交审批信息
     *
     * @param companyId   公司id
     * @param memberId    成员id
     * @param modelId     模型主键
     * @param jsonData    要提交的审批数据
     * @param sendUserIds 要推送的审批人主键，多个以英文，隔开
     * @param sendCopyIds 要推送的抄送人主键，多个以英文，隔开
     * @return
     * @throws Exception
     */
    boolean submit(Long companyId, Long memberId, Long modelId, String jsonData, String sendUserIds, String sendCopyIds) throws Exception;

    /**
     * 获取审批数据列表
     *
     * @param page            分页对象  current 页码, size页大小
     * @param companyId       公司id
     * @param modelId         模型主键, 审批类型, 可空(全部)
     * @param state           审批状态  0:审批中 1:审批完成 2:已撤回, 可空(全部)
     * @param title           审批标题
     * @param createTimeStart 发起时间_开始
     * @param createTimeEnd   发起时间_结束
     * @param finishTimeStart 完成时间_开始
     * @param finishTimeEnd   完成时间_结束
     * @return 分页列表
     * @throws Exception 异常
     */
    ApprovalPageVO page(Page<Approval> page, Long companyId, Long modelId, Integer state, String title, String createTimeStart, String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws Exception;

    /**
     * 删除审批数据
     *
     * @param approvalId 审批主键
     * @return
     * @throws Exception
     */
    boolean delete(Long approvalId) throws Exception;

    /**
     * 审批数据导出
     *
     * @param companyId       公司id
     * @param memberId        成员id
     * @param modelId         模型主键, 审批类型, 可空(全部)
     * @param state           审批状态  0:审批中 1:审批完成 2:已撤回, 可空(全部)
     * @param title           审批标题
     * @param createTimeStart 发起时间_开始
     * @param createTimeEnd   发起时间_结束
     * @param finishTimeStart 完成时间_开始
     * @param finishTimeEnd   完成时间_结束
     * @return
     * @throws Exception
     */
    BaseExModel createApprovalExcel(Long companyId, Long memberId, Long modelId, Integer state, String title, String createTimeStart,
                                    String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws Exception;
}