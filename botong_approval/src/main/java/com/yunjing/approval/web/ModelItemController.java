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
public class ModelItemController extends BaseController{

    @Autowired
    private IModelItemService modelItemService;

    @GetMapping("/get")
    public ResponseEntityWrapper getItem(@RequestParam("modelId") Long modelId) throws Exception {
        return success(modelItemService.getModelItem(modelId));
    }

    @PostMapping("/save")
    public ResponseEntityWrapper saveItem(@RequestParam("oid") Long orgId, @RequestParam("uid") Long userId, @RequestParam("json") String json) throws Exception {
        return success(modelItemService.saveModelItem(orgId, userId, json));
    }
}
