package com.yunjing.approval.api;

import com.yunjing.approval.processor.task.async.ApprovalPushTask;
import com.yunjing.approval.service.IOrgModelService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxp
 */
@RestController

public class TestController extends BaseController{


    @Autowired
    private IOrgModelService orgModelService;
    /**
     * 审批模板初始化
     *
     * @param companyId
     * @return
     * @throws Exception
     */
    @PostMapping("/test/init")
    public ResponseEntityWrapper init(@RequestParam("companyId") String companyId) throws Exception {
        boolean approvalModel = orgModelService.createApprovalModel(companyId);
        return success(approvalModel);
    }

    /**
     * 审批模板删除
     *
     * @param companyId
     * @return
     * @throws Exception
     */
    @PostMapping("/test/delete")
    public ResponseEntityWrapper delete(@RequestParam("companyId") String companyId) throws Exception {
        boolean approvalModel = orgModelService.deleteApprovalModel(companyId);
        return success(approvalModel);
    }

    @Autowired
    private ApprovalPushTask pushTask;
    @PostMapping("/test/push")
    public ResponseEntityWrapper testPush(@RequestParam("companyId") String companyId,
                                          @RequestParam("memberId") String memberId,@RequestParam("approvalId") String approvalId) throws Exception {

        pushTask.init(approvalId, companyId, memberId).run();
        return success();
    }

}
