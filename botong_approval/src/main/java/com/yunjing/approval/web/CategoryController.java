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
     * @param companyId    公司id
     * @param categoryName 审批类别名称
     * @return
     */
    @PostMapping("/create-or-rename")
    public ResponseEntityWrapper createOrEdit(@RequestParam("companyId") Long companyId, Long categoryId, @RequestParam("categoryName") String categoryName) throws Exception {

        return success(modelCategoryService.createOrEditCategory(companyId, categoryId, categoryName));
    }

    /**
     * 删除分组
     *
     * @param companyId  公司id
     * @param categoryId 分组主键
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper delete(@RequestParam("companyId") Long companyId, @RequestParam("categoryId") Long categoryId) throws Exception {

        return success(modelCategoryService.deleteCategory(companyId, categoryId));
    }

    /**
     * 查询分组列表
     *
     * @param companyId 公司id
     * @return
     */
    @GetMapping("/list")
    public ResponseEntityWrapper getCategoryList(@RequestParam("companyId") Long companyId) throws Exception {

        return success(modelCategoryService.getCategoryList(companyId));
    }

    /**
     * 分组排序
     *
     * @param companyId 公司id
     * @param sortArray 序号数组
     * @return
     */
    @PostMapping("/sort")
    public ResponseEntityWrapper sort(@RequestParam("companyId") Long companyId, @RequestParam("sortArray") String sortArray) throws Exception {

        return success(modelCategoryService.sortedCategory(companyId, sortArray));
    }


}
