package com.yunjing.botong.log.api;

import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.botong.log.processor.mq.producer.LogTemplateCreateProducer;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.botong.log.vo.LogTemplateItemVo;
import com.yunjing.botong.log.vo.LogTemplateVo;
import com.yunjing.message.model.Message;
import com.yunjing.message.share.org.OrgAppMessage;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王开亮
 * @date 2018/3/30 15:34
 */
@RestController
@RequestMapping("/log/template")
public class LogTemplateApi extends BaseController {

    @Autowired
    private LogTemplateService logTemplateService;

    @Autowired
    private AppCenterService appCenterService;

    @Value("${botong.log.appId}")
    private String appId;

    @Autowired
    private LogTemplateCreateProducer logTemplateCreateProducer;


    @GetMapping("/verify-manager")
    public ResponseEntityWrapper verifyManager(@RequestParam("memberId") String memberId) {
        boolean manager = appCenterService.isManager(appId, memberId);
        return success(manager);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntityWrapper<String> createLogTemplate(@RequestBody LogTemplateParam logTemplateParam) {
        return this.success(this.logTemplateService.createLogTemplate(logTemplateParam));
    }

    @RequestMapping(value = "/query-all", method = RequestMethod.GET)
    public ResponseEntityWrapper<PageWrapper<LogTemplateItemVo>> queryAll(@RequestParam("orgId") String orgId, @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize) {
        return this.success(this.logTemplateService.queryAllLogTemplate(orgId, pageNo, pageSize));
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntityWrapper<LogTemplateVo> query(@RequestParam("id") String id) {
        return this.success(this.logTemplateService.queryLogTemplate(id));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntityWrapper<Boolean> update(@RequestBody LogTemplateParam logTemplateParam) {
        return this.logTemplateService.updateLogTemplate(logTemplateParam) ? this.success(true) : this.error(500, "模板更新失败");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntityWrapper<Boolean> delete(@RequestParam("id") String id) {
        return this.success(this.logTemplateService.deleteLogTemplate(id));
    }

    @RequestMapping(value = "/org-init", method = RequestMethod.POST)
    public ResponseEntityWrapper orgInit(@RequestParam("what") String what, @RequestParam("orgId")  String orgId){
        OrgAppMessage orgAppMessage = new OrgAppMessage();
        orgAppMessage.setCompanyId(orgId);
        Message message = Message.obtain(what, System.currentTimeMillis(), orgAppMessage);
        logTemplateCreateProducer.sendMessage(message);
        return this.success();
    }
}
