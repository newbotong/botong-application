package com.yunjing.botong.log.api;

import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.botong.log.vo.LogTemplateItemVo;
import com.yunjing.botong.log.vo.LogTemplateVo;
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

    @RequestMapping(value = "/query-all",method = RequestMethod.GET)
    public PageWrapper<LogTemplateItemVo> queryAll(@RequestParam("orgId") long orgId, @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize){
        return this.logTemplateService.queryAllLogTemplate(orgId,pageNo,pageSize);
    }

    @RequestMapping(value = "/query",method = RequestMethod.GET)
    public ResponseEntityWrapper<LogTemplateVo> query(@RequestParam("id") long id){
        return this.success(this.logTemplateService.queryLogTemplate(id));
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public ResponseEntityWrapper<Boolean> update(@RequestParam("id") long id){
        return null;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public ResponseEntityWrapper<Boolean> delete(@RequestParam("id") long id){
        return this.success(this.logTemplateService.deleteLogTemplate(id));
    }

}
