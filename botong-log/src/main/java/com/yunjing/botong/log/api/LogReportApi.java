package com.yunjing.botong.log.api;

import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.params.ManagerListParam;
import com.yunjing.botong.log.service.LogReportService;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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

    @Value("${botong.log.appId}")
    private String appId;


    @Autowired
    private LogReportService logReportService;

    /**
     * @param memberId   用户所有企业的成员id
     * @param orgId      企业编号
     * @param pageNo     页码
     * @param pageSize   页大小
     * @param submitType 日报模版类型（0-全部 1-日报 2-周报 3-月报）
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return
     */
    @GetMapping("/list")
    public ResponseEntityWrapper list(@RequestParam String memberId,
                                      @RequestParam String orgId,
                                      @RequestParam Integer pageNo,
                                      @RequestParam Integer pageSize,
                                      @RequestParam(required = false, defaultValue = "1") Integer submitType,
                                      @RequestParam(required = false, defaultValue = "0") Long startDate,
                                      @RequestParam(required = false, defaultValue = "0") Long endDate) {
        if (submitType == 0) {
            submitType = 1;
        }
        PageWrapper query = logReportService.query(memberId, orgId, appId, pageNo, pageSize, submitType, startDate, endDate);
        return success(query);
    }


    /**
     * 日志管理已提交列表
     *
     * @param param
     * @return
     */
    @PostMapping("/manager-submit-list")
    public ResponseEntityWrapper submitList(@RequestBody ManagerListParam param) {
        PageWrapper<Member> wrapper = logReportService.submitList(param.getMemberId(), param.getOrgId(), appId, param.getSubmitType(), param.getDate(), param.getPageNo(), param.getPageSize());
        return success(wrapper);
    }

    /**
     * 日志管理未提交列表
     *
     * @param param
     * @return
     */
    @PostMapping("/manager-unsubmit-list")
    public ResponseEntityWrapper unSubmitList(@RequestBody ManagerListParam param) {
        PageWrapper<Member> wrapper = logReportService.unSubmitList(param.getMemberId(), param.getOrgId(), appId, param.getSubmitType(), param.getDate(), param.getPageNo(), param.getPageSize());
        return success(wrapper);
    }
}
