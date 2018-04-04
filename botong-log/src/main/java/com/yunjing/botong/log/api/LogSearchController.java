package com.yunjing.botong.log.api;

import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.processor.mq.producer.RemindMessageProducer;
import com.yunjing.botong.log.service.ILogSearchService;
import com.yunjing.message.model.Message;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * <p> 日志查询
 * </p>
 *
 * @author jingwj
 * @since 2018/3/28.
 */
@RestController
@RequestMapping("/log/search")
public class LogSearchController extends BaseController {

    @Autowired
    private ILogSearchService iLogSearchService;

    @PostMapping("/recevied-page")
    public ResponseEntityWrapper receviedPage(@RequestBody ReceviedParam receviedParam) {
        return success(iLogSearchService.receivePage(receviedParam));
    }
}
