package com.yunjing.info.init;

import com.yunjing.info.service.impl.InfoCatalogServiceImpl;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 初始化公司类目
 *
 * @author 李双喜
 * @date 2018/4/4 13:49
 */
@RestController
@RequestMapping("/info/init")
public class CompanyCatalogInit extends BaseController {


    @Autowired
    private InfoCatalogServiceImpl infoCatalogService;

    /**
     * 手动初始化企业目录结构
     *
     * @param orgId 企业id
     * @return
     */
    @RequestMapping("/company-catalog")
    public ResponseEntityWrapper initCompany(@RequestParam String orgId){
        infoCatalogService.initCompany(orgId);
        return success();
    }
}
