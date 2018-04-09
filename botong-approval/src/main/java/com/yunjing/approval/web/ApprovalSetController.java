package com.yunjing.approval.web;

import com.yunjing.approval.service.IApprovalSetsService;
import com.yunjing.approval.service.IModelService;
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
@RequestMapping("/approval/set")
public class ApprovalSetController extends BaseController {


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
     * @return ResponseEntityWrapper
     */
    @PostMapping("/get")
    public ResponseEntityWrapper getSetItem(@RequestParam("modelId") String modelId) throws Exception {

        return success(approvalSetsService.getApprovalSet(modelId));
    }

    /**
     * 保存审批设置项
     *
     * @param modelId 模型主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save")
    public ResponseEntityWrapper saveSetItem(@RequestParam("modelId") String modelId, @RequestParam("setting") int setting) throws Exception {

        return success(approvalSetsService.saveApprovalSets(modelId, setting));
    }

    /**
     * 设置审批流程
     *
     * @param modelId 模型主键
     * @return ResponseEntityWrapper
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
     * @return ResponseEntityWrapper
     */
    @PostMapping("/deleteProcess")
    public ResponseEntityWrapper deleteProcess(@RequestParam("modelId") String modelId, @RequestParam(value = "conditionId", required = false) String conditionId) throws Exception {
        return success(processService.delete(modelId, conditionId));
    }

    /**
     * 删除审批流程人
     *
     * @param companyId 公司id
     * @param memberId  成员id
     */
    @PostMapping("/deleteProcessUser")
    public void deleteProcessUser(@RequestParam("companyId") String companyId, @RequestParam("memberId") String memberId) {
        processService.deleteProcessUser(companyId, memberId);
    }
}
