package com.yunjing.approval.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author roc
 * @date 2017/12/21
 */
@RestController
@RequestMapping("/approval/data")
public class ApprovalDataController extends BaseController {

    @Autowired
    private IApprovalService approvalService;

    /**
     * 获取审批数据列表
     *
     * @param page            分页对象, 可空 页码current 默认1页    大小size 默认10
     * @param oid             企业主键
     * @param mid             模型主键, 审批类型, 可空(全部)
     * @param state           审批状态  0:审批中 1:审批完成 2:已撤回, 可空(全部)
     * @param title           审批标题, 可空
     * @param createTimeStart 发起时间_开始, 可空
     * @param createTimeEnd   发起时间_结束, 可空
     * @param finishTimeStart 完成时间_开始, 可空
     * @param finishTimeEnd   完成时间_结束, 可空
     * @return 分页列表
     */
    @PostMapping("/list")
    public ResponseEntityWrapper page(@ModelAttribute(value = "page") Page<Approval> page, @RequestParam("oid") String oid, String mid, Integer state, String title, String createTimeStart, String createTimeEnd, String finishTimeStart, String finishTimeEnd) throws Exception {
        return success(approvalService.page(page, oid, mid, state, title, createTimeStart, createTimeEnd, finishTimeStart, finishTimeEnd));
    }

    /**
     * 删除审批数据
     *
     * @param approvalId 审批主键
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper deleteApproval(@RequestParam("approvalId") String approvalId) throws Exception {

        return success(approvalService.delete(approvalId));
    }

}
