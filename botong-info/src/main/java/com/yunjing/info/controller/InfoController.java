package com.yunjing.info.controller;

import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资讯的Web端接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:24
 */
@RestController
@RequestMapping("/info")
public class InfoController extends BaseController {
    @Autowired
    private InfoContentService infoContentService;

    @Autowired
    private InfoCatalogService infoCatalogService;
//    /**
//     * 新增类目
//     */
//    @RequestMapping("/insert-category")
//    public ResponseEntityWrapper insertCategory(@RequestBody InfoCategoryParam infoCategoryParam) throws BaseException{
//        infoCatalogService.insertCategory(infoCategoryParam);
//        return success();
//    }
//
//    /**
//     * 增加内容
//     */
//    @RequestMapping("/insert-content")
//    public ResponseEntityWrapper insertContent(@RequestBody )throws BaseException{
//        return null;
//    }
}
