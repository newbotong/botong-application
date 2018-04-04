package com.yunjing.botong.log.api;

import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.service.LogReportService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private LogReportService logReportService;

    /**
     * @param memberId   用户所有企业的成员id
     * @param orgId      企业编号
     * @param pageNo     页码
     * @param pageSize   页大小
     * @param submitType 日报模版类型（1-日报 2-周报 3-月报）
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntityWrapper list(long memberId,
                                      long orgId,
                                      long appId,
                                      int pageNo,
                                      int pageSize,
                                      @RequestParam(required = false, defaultValue = "0") int submitType,
                                      @RequestParam(required = false, defaultValue = "0") long startDate,
                                      @RequestParam(required = false, defaultValue = "0") long endDate) {

        PageWrapper query = logReportService.query(memberId, orgId, appId, pageNo, pageSize, submitType, startDate, endDate);
        return success(query);
    }
}
