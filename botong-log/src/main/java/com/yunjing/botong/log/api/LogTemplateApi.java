package com.yunjing.botong.log.api;

import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.botong.log.vo.LogTemplateItem;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author auth
 * @date 2018/3/30 15:34
 */
@RestController
@RequestMapping("/log/template")
public class LogTemplateApi extends BaseController{

    @Autowired
    private LogTemplateService logTemplateService;

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public ResponseEntityWrapper<String> createLogTemplate(@RequestBody LogTemplateParam logTemplateParam){

        return this.success(this.logTemplateService.createLogTemplate(logTemplateParam));
    }

    @RequestMapping(value = "/queryAll",method = RequestMethod.POST)
    public PageWrapper<LogTemplateItem> queryAll(@RequestBody LogTemplateParam logTemplateParam){
        return null;
    }

    @RequestMapping(value = "/query",method = RequestMethod.POST)
    public ResponseEntityWrapper query(@RequestParam("id") long id){
        return null;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public ResponseEntityWrapper<Boolean> update(@RequestParam("id") long id){
        return null;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public ResponseEntityWrapper<Boolean> delete(@RequestParam("id") long id){
        return null;
    }

}
