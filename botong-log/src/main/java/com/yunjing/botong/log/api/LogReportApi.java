package com.yunjing.botong.log.api;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * <p> 日志报表
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@RestController
@RequestMapping("/log/report")
public class LogReportApi extends BaseController {

    /**
     * @param memberId   用户所有企业的成员id
     * @param submitType 日报模版类型（1-日报 2-周报 3-月报）
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntityWrapper list(long memberId,
                                      @RequestParam(required = false) int submitType,
                                      @RequestParam(required = false) long startDate,
                                      @RequestParam(required = false) long endDate) {
        return success();
    }
}
