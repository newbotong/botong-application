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
     * 获取审批条件及对应审批人
     *
     * @param modelId 模型主键
     * @return
     * @throws Exception
     */
    @PostMapping("/get")
    public ResponseEntityWrapper get(@RequestParam String modelId) throws Exception {
        return success(cdnService.getJudgeList(modelId));
    }

    /**
     * 保存审批条件及对应的审批人
     *
     * @param modelId   模型主键
     * @param judge     选择的审批条件选项
     * @param memberIds 审批人集合
     * @return
     * @throws Exception
     */
    @PostMapping("/save")
    public ResponseEntityWrapper save(@RequestParam String modelId, @RequestParam String judge, @RequestParam("memberIds") String memberIds,
                                      @RequestParam(value = "conditionIds", required = false) String conditionIds) throws Exception {
        return success(cdnService.save(modelId, judge, memberIds, conditionIds));
    }

    /**
     * 清除审批条件值及对应审批流程人
     *
     * @param modelId      模型主键
     * @param conditionIds 条件主键，以逗号隔开
     * @return ResponseEntityWrapper
     */
    @PostMapping("/delete-process")
    public ResponseEntityWrapper deleteProcess(@RequestParam("modelId") String modelId, @RequestParam("conditionIds") String conditionIds) throws Exception {
        return success(cdnService.deleteProcess(modelId, conditionIds));
    }

}
