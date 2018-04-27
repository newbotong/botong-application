package com.yunjing.botong.log.api;


import com.yunjing.botong.log.params.LogParam;
import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.botong.log.service.LogService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志控制器
 * @author 王开亮
 * @date 2018/4/9 9:44
 */
@RestController
@RequestMapping("/log/log")
public class LogApi extends BaseController {

    @Autowired
    private LogService logService;

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public ResponseEntityWrapper<Long> createLog(@RequestBody LogParam logParam){
        if(CollectionUtils.isEmpty(logParam.getSendToUser())){
            this.error(500,"接收人不能为空");
        }
        return this.success(this.logService.createLog(logParam));
    }


}
