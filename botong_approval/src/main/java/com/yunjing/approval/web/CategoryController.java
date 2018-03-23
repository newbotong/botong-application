package com.yunjing.approval.web;

import com.yunjing.approval.service.IModelCategoryService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 审批分组相关操作
 *
 * @author 刘小鹏
 * @date 2018/03/21
 */
@RestController
@RequestMapping("/approval/category")
public class CategoryController extends BaseController {

    @Autowired
    private IModelCategoryService modelCategoryService;

    /**
     * 新增或者重命名分组
     *
     * @param oid          企业主键
     * @param categoryName 审批类别名称
     * @return
     */
    @PostMapping("/create_or_rename")
    public ResponseEntityWrapper createOrEdit(@RequestParam("oid") Long oid, Long categoryId, @RequestParam("categoryName") String categoryName) throws Exception {

        return success(modelCategoryService.createOrEditCategory(oid, categoryId, categoryName));
    }

    /**
     * 删除分组
     *
     * @param categoryId 分组主键
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper delete(@RequestParam("oid") Long oid, @RequestParam("categoryId") Long categoryId) throws Exception {

        return success(modelCategoryService.deleteCategory(oid, categoryId));
    }

    /**
     * 查询分组列表
     *
     * @param oid 企业主键
     * @return
     */
    @GetMapping("/list")
    public ResponseEntityWrapper getCategoryList(@RequestParam("oid") Long oid) throws Exception {

        return success(modelCategoryService.getCategoryList(oid));
    }

    /**
     * 分组排序
     *
     * @param oid       企业主键
     * @param sortArray 序号数组
     * @return
     */
    @PostMapping("/sort")
    public ResponseEntityWrapper sort(@RequestParam("oid") Long oid, @RequestParam("sortArray") String sortArray) throws Exception {

        return success(modelCategoryService.sortedCategory(oid, sortArray));
    }


}
