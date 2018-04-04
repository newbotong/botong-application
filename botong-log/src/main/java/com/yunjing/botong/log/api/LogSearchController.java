package com.yunjing.botong.log.api;

import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.processor.mq.producer.RemindMessageProducer;
import com.yunjing.botong.log.service.ILogSearchService;
import com.yunjing.message.model.Message;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 我收到的日志明细列表
     * @param receviedParam     接收参数
     * @return
     */
    @PostMapping("/recevied-page")
    public ResponseEntityWrapper receviedPage(@RequestBody ReceviedParam receviedParam) {
        return success(iLogSearchService.receivePage(receviedParam));
    }

    /**
     * 我发送的日志明细列表
     * @param receviedParam
     * @return
     */
    @PostMapping("/send-page")
    public ResponseEntityWrapper sendPage(@RequestBody ReceviedParam receviedParam) {
        return success(iLogSearchService.receivePage(receviedParam));
    }

    /**
     * 日志设为已读
     * @param logId         日志id
     * @return              成功与否
     */
    @PostMapping("/read")
    public ResponseEntityWrapper read(@RequestParam String logId) {
        return success();
    }
}
