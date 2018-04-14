package com.yunjing.approval.service;


import com.alibaba.fastjson.JSONArray;
import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.excel.BaseExModel;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.model.vo.ApprovalVO;
import com.yunjing.approval.param.DataParam;
import com.yunjing.mommon.wrapper.PageWrapper;

import java.util.List;

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
    boolean submit(String companyId, String memberId, String modelId, JSONArray jsonData, String sendUserIds, String sendCopyIds) throws Exception;

    /**
     * 获取审批数据列表
     *
     * @param dataParam 设置查询参数
     * @return 分页列表
     * @throws Exception 抛异常
     */
    PageWrapper<ApprovalVO> page(DataParam dataParam) throws Exception;

    /**
     * 删除审批数据
     *
     * @param approvalId 审批主键
     * @return
     * @throws Exception
     */
    boolean delete(String approvalId) throws Exception;

    /**
     * 审批数据导出
     * @param dataParam 设置参数
     * @return
     * @throws Exception
     */
    BaseExModel createApprovalExcel(DataParam dataParam) throws Exception;
}
