package com.yunjing.approval.web;

import com.yunjing.approval.service.IProcessService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 刘小鹏
 * @date 2018/04/04
 */
@RestController
@RequestMapping("/web/approval/set")
public class ApprovalSetController extends BaseController {

    @Autowired
    private IProcessService processService;

    /**
     * 获取设置的默认审批人及抄送人
     *
     * @param modelId 模型主键
     * @return
     * @throws Exception
     */
    @PostMapping("/get-default-process")
    public ResponseEntityWrapper getDefaultApproverAndCopy(@RequestParam String modelId) throws Exception {
        return success(processService.getDefaultApprover(modelId));
    }

    /**
     * 设置默认审批人和抄送人
     *
     * @param modelId     模型主键
     * @param approverIds 审批人ID 多个逗号隔开
     * @param copyIds     抄送人ID 多个逗号隔开
     * @return ResponseEntityWrapper
     */
    @PostMapping("/update-default-process")
    public ResponseEntityWrapper saveDefaultApprover(@RequestParam("modelId") String modelId,
                                                     @RequestParam(value = "approverIds", required = false) String approverIds,
                                                     @RequestParam(value = "copyIds", required = false) String copyIds) throws Exception {

        return success(processService.saveDefaultApprover(modelId, approverIds, copyIds));
    }

    /**
     * 清除默认审批流程人
     *
     * @param modelId 模型主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/delete-default-process")
    public ResponseEntityWrapper deleteProcess(@RequestParam("modelId") String modelId) throws Exception {
        return success(processService.delete(modelId, null));
    }

}
