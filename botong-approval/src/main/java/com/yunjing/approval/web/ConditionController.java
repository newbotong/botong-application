package com.yunjing.approval.web;

import com.yunjing.approval.service.IConditionService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
@RestController
@RequestMapping("/web/approval/condition")
public class ConditionController extends BaseController {

    @Autowired
    private IConditionService cdnService;

    /**
     * 获取审批条件
     *
     * @param modelId 模型主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/get")
    public ResponseEntityWrapper get(@RequestParam String modelId) {
        return success(cdnService.getJudgeList(modelId));
    }

    /**
     * 编辑某一条时--获取审批条件及对应审批人
     *
     * @param modelId 模型主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/get-approver")
    public ResponseEntityWrapper getConditionAndApprover(@RequestParam("modelId") String modelId, @RequestParam("conditionId") String conditionId) {
        return success(cdnService.getConditionAndApprover(modelId, conditionId));
    }

    /**
     * 获取审批条件及对应审批人的列表
     *
     * @param modelId 模型主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/get-approver-list")
    public ResponseEntityWrapper getConditionAndApproverList(@RequestParam String modelId) {
        return success(cdnService.getConditionAndApproverList(modelId));
    }

    /**
     * 审批条件列表优先级排序
     *
     * @param modelId   模型主键
     * @param sortArray 序号数组([{"conditionId":"sort"},])
     * @return ResponseEntityWrapper
     */
    @PostMapping("/sort")
    public ResponseEntityWrapper sort(@RequestParam("modelId") String modelId, @RequestParam("sortArray") String sortArray) {
        return success(cdnService.sortedCondition(modelId, sortArray));
    }

    /**
     * 保存审批条件及对应的审批人
     *
     * @param modelId   模型主键
     * @param judge     选择的审批条件选项
     * @param memberIds 审批人集合
     * @param conditionId 条件id
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save")
    public ResponseEntityWrapper save(@RequestParam String modelId, @RequestParam String judge, @RequestParam("memberIds") String memberIds,
                                      @RequestParam(value = "conditionId", required = false) String conditionId) {
        return success(cdnService.saveSetsCondition(modelId, judge, memberIds, conditionId));
    }

    /**
     * 清除审批条件值及对应审批流程人
     *
     * @param modelId     模型主键
     * @param conditionId 条件主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/delete-process")
    public ResponseEntityWrapper deleteProcess(@RequestParam("modelId") String modelId, @RequestParam("conditionId") String conditionId) {
        return success(cdnService.deleteProcess(modelId, conditionId));
    }

}
