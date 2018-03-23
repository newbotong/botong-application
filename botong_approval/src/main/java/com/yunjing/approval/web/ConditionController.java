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
 * @author roc
 * @date 2017/12/21
 */
@RestController
@RequestMapping("/approval/cdn")
public class ConditionController extends BaseController {

    @Autowired
    private IConditionService cdnService;

    /**
     * 获取审批条件
     *
     * @param modelId 模型主键
     * @return
     * @throws Exception
     */
    @PostMapping("/get")
    public ResponseEntityWrapper get(@RequestParam Long modelId) throws Exception {
        return success(cdnService.getJudgeList(modelId));
    }

    /**
     * 保存审批条件
     *
     * @param modelId 模型主键
     * @param field   字段名称
     * @param numbers 天数 多个天数以因为逗号（,）分隔
     * @return
     * @throws Exception
     */
    @PostMapping("/save")
    public ResponseEntityWrapper save(@RequestParam Long modelId, @RequestParam String field, @RequestParam(required = false) String numbers) throws Exception {
        return success(cdnService.save(modelId, field, numbers));
    }

    /**
     * 删除审批条件
     * @param modelId    模型主键
     * @param conditionId 审批条件
     * @return
     * @throws Exception
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper delete(@RequestParam Long modelId, @RequestParam(required = false) Long conditionId) throws Exception {
        return success(cdnService.delete(modelId, conditionId));
    }
}
