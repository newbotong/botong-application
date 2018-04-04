package com.yunjing.approval.web;


import com.yunjing.approval.service.IModelItemService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 刘小鹏
 * @date 2018/03/22
 */
@RestController
@RequestMapping("/approval/model/item")
public class ModelItemController extends BaseController {

    @Autowired
    private IModelItemService modelItemService;

    /**
     * 获取审批项模型列表
     *
     * @param modelId 审批模型id
     * @return ResponseEntityWrapper
     * @throws Exception 抛异常
     */
    @GetMapping("/get")
    public ResponseEntityWrapper getItem(@RequestParam("modelId") Long modelId) throws Exception {
        return success(modelItemService.getModelItem(modelId));
    }

    /**
     * 保存审批项模型
     *
     * @param companyId 公司id
     * @param memberId  成员id
     * @param json      审批项json数据
     * @return ResponseEntityWrapper
     * @throws Exception 抛异常
     */
    @PostMapping("/save")
    public ResponseEntityWrapper saveItem(@RequestParam("companyId") Long companyId, @RequestParam("memberId") Long memberId, @RequestParam("json") String json) throws Exception {
        return success(modelItemService.saveModelItem(companyId, memberId, json));
    }
}
