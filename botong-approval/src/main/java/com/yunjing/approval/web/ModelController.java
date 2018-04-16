package com.yunjing.approval.web;

import com.yunjing.approval.service.IModelService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 审批模板相关操作
 *
 * @author 刘小鹏
 * @date 2018/03/21
 */
@RestController
@RequestMapping("/approval/model")
public class ModelController extends BaseController {

    @Autowired
    private IModelService modelService;

    /**
     * 获取审批模板列表
     *
     * @param companyId 公司id
     * @return ResponseEntityWrapper
     */
    @GetMapping("/list")
    public ResponseEntityWrapper getList(@RequestParam("companyId") String companyId) throws Exception {

        return success(modelService.findModelList(companyId));
    }

    /**
     * 分组中的审批模板排序
     *
     * @param categoryId 分组主键
     * @param sortArray  序号数组
     * @return ResponseEntityWrapper
     */
    @PostMapping("/sort")
    public ResponseEntityWrapper sort(@RequestParam("categoryId") String categoryId, @RequestParam("sortArray") String sortArray) throws Exception {

        return success(modelService.sortedModel(categoryId, sortArray));
    }

    /**
     * 移动审批模板到其他分组
     *
     * @param categoryId 要移动到的分组主键
     * @param modelId    模型主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/move-to")
    public ResponseEntityWrapper moveTo(@RequestParam("categoryId") String categoryId, @RequestParam("modelId") String modelId) throws Exception {

        return success(modelService.moveModel(categoryId, modelId));
    }

    /**
     * 设置该审批模板是否停用
     *
     * @param modelId    模型主键
     * @param isDisabled 是否停用（0：正常，1,：停用）
     * @return ResponseEntityWrapper
     */
    @PostMapping("/is-disabled")
    public ResponseEntityWrapper updateIsDisabled(@RequestParam("modelId") String modelId,
                                                  @RequestParam("isDisabled") Integer isDisabled) throws Exception {

        return success(modelService.updateIsDisabled(modelId, isDisabled));
    }

    /**
     * 修改可见范围
     *
     * @param modelId    模型主键
     * @param  deptIds（多个部门以英文,隔开）
     * @param  userIds（多个用户以英文,隔开）
     * @return ResponseEntityWrapper
     */
    @PostMapping("/update-visible-range")
    public ResponseEntityWrapper updateVisibleRange(@RequestParam("modelId") String modelId,
                                                  @RequestParam("deptIds") String deptIds,
                                                    @RequestParam("userIds") String userIds) throws Exception {

        return success(modelService.updateVisibleRange(modelId, deptIds,userIds));
    }

    /**
     * 获取系统审批模板的logo列表
     *
     * @return ResponseEntityWrapper
     */
    @PostMapping("/get-logo")
    public ResponseEntityWrapper getLogo() throws Exception {

        return success(modelService.getLogo());
    }

}
