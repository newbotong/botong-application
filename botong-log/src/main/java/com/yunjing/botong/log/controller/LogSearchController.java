package com.yunjing.botong.log.controller;

import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.service.ILogSearchService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * <p> 日志管理
 * </p>
 *
 * @author jingwj
 * @since 2018/3/28.
 */
@RestController
@RequestMapping("/web/log/manage")
public class LogSearchController extends BaseController {

    @Autowired
    private ILogSearchService iLogSearchService;

    /**
     * 我收到的日志明细列表
     * @param receviedParam     接收参数
     * @return
     */
    @PostMapping("/find-page")
    public ResponseEntityWrapper receviedPage(@RequestBody ReceviedParam receviedParam) {
        return success(iLogSearchService.receivePage(receviedParam));
    }




    /**
     * 日志设为已读
     * @param receviedParam 日志参数对象
     * @return              成功与否
     */
    @PostMapping("/read-all")
    public ResponseEntityWrapper readAll(@RequestBody ReceviedParam receviedParam) {
        return success(iLogSearchService.read(receviedParam));
    }

    /**
     * 删除日志
     * @param receviedParam         日志id
     * @return              成功与否
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper delete(@RequestBody ReceviedParam receviedParam) {
        return success(iLogSearchService.delete(receviedParam.getLogId(), receviedParam.getUserId()));
    }
}
