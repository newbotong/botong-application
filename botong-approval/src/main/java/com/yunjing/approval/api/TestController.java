package com.yunjing.approval.api;

import com.yunjing.approval.processor.task.async.ApprovalPushTask;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.IModelCategoryService;
import com.yunjing.approval.service.IOrgModelService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxp
 */
@RestController

public class TestController extends BaseController {


    @Autowired
    private IOrgModelService orgModelService;
    @Autowired
    private IModelCategoryService categoryService;

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
                                          @RequestParam("memberId") String memberId, @RequestParam("approvalId") String approvalId) throws Exception {

        pushTask.init(approvalId, companyId, memberId).run();
        return success();
    }

    /**
     * 给旧数据初始化分组信息并更新model表分组字段信息
     * @param companyId 公司id
     * @return
     */
    @GetMapping("/test/init/category")
    public ResponseEntityWrapper test(@RequestParam("companyId") String companyId) {

        return success(categoryService.initModelCategory(companyId));
    }
}
