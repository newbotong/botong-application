package com.yunjing.approval.web;

import com.yunjing.approval.service.IApprovalSetsService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.service.IProcessService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author roc
 * @date 2017/12/21
 */
@RestController
@RequestMapping("/approval")
public class ApprovalController extends BaseController {

    @Autowired
    private IModelService modelService;
    @Autowired
    private IApprovalSetsService approvalSetsService;
    @Autowired
    private IProcessService processService;

    /**
     * 获取审批设置项
     *
     * @param modelId 模型主键
     * @return
     */
    @PostMapping("/getSetItem")
    public ResponseEntityWrapper getSetItem(@RequestParam("modelId") String modelId) throws Exception {

        return success(approvalSetsService.getApprovalSet(modelId));
    }

    /**
     * 保存审批设置项
     *
     * @param modelId 模型主键
     * @return
     */
    @PostMapping("/saveSetItem")
    public ResponseEntityWrapper saveSetItem(@RequestParam("modelId") String modelId, @RequestParam("setting") int setting) throws Exception {

        return success(approvalSetsService.saveApprovalSets(modelId, setting));
    }

    /**
     * 设置审批流程
     *
     * @param modelId 模型主键
     * @return
     */
    @PostMapping("/updateProcess")
    public ResponseEntityWrapper updateProcess(@RequestParam("modelId") String modelId,
                                @RequestParam(value = "conditionId", required = false) String conditionId,
                                @RequestParam(value = "userArray", required = false) String userArray) throws Exception {

        return success(processService.updateProcess(modelId, conditionId, userArray));
    }

    /**
     * 清除审批流程
     *
     * @param modelId     模型主键
     * @param conditionId 条件主键
     * @return
     */
    @PostMapping("/deleteProcess")
    public ResponseEntityWrapper deleteProcess(@RequestParam("modelId") String modelId, @RequestParam(value = "conditionId", required = false) String conditionId) throws Exception {
        return success(processService.delete(modelId, conditionId));
    }

    /**
     * 删除审批流程人
     *
     * @param oid 企业主键
     * @param uid 用户主键
     */
    @PostMapping("/deleteProcessUser")
    public void deleteProcessUser(@RequestParam("oid") String oid, @RequestParam("uid") String uid) {
        processService.deleteProcessUser(oid, uid);
    }
}