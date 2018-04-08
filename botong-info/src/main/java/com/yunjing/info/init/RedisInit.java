package com.yunjing.info.init;

import com.yunjing.info.service.impl.InfoCatalogServiceImpl;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Redis初始化字典
 *
 * @author 李双喜
 * @date 2018/4/3 10:52
 */
@RequestMapping("/info/init")
@RestController
public class RedisInit extends BaseController {

    @Autowired
    private InfoCatalogServiceImpl infoCatalogService;

    @RequestMapping("/catalog")
    public ResponseEntityWrapper initCatalog() throws BaseException{
        infoCatalogService.init();
        return success();
    }
}
