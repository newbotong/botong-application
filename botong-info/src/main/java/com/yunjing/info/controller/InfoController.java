package com.yunjing.info.controller;

import com.yunjing.info.dto.CompanyRedisCatalogDto;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资讯的Web端接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:24
 */
@RestController
@RequestMapping("/web/info")
public class InfoController extends BaseController {
    @Autowired
    private InfoContentService infoContentService;

    @Autowired
    private InfoCatalogService infoCatalogService;

    /**
     * 新增资讯接口
     *
     * @param infoCategoryParam 入参对象
     * @return
     */
    @PostMapping("/insert")
    public ResponseEntityWrapper insertInfo(@RequestBody InfoCategoryParam infoCategoryParam) throws BaseException {
        BeanFieldValidator.getInstance().ignore().validate(infoCategoryParam);
        infoContentService.insertInfo(infoCategoryParam);
        return success();
    }

    /**
     * 查询企业类目
     *
     * @param orgId 企业类目id
     * @return
     */
    @PostMapping("/parent")
    public ResponseEntityWrapper selectParent(@RequestParam Long orgId) {
        List<CompanyRedisCatalogDto> list = infoCatalogService.selectParentCatalog(orgId);
        return success(list);
    }

    /**
     * 修改资讯信息
     *
     * @param infoCategoryParam  实体入参
     * @return
     * @throws BaseException
     */
    @PostMapping("/edit")
    public ResponseEntityWrapper infoEdit(@RequestBody InfoCategoryParam infoCategoryParam) throws BaseException {
        BeanFieldValidator.getInstance().ignore().validate(infoCategoryParam);
        infoContentService.infoEdit(infoCategoryParam);
        return success();
    }
}
