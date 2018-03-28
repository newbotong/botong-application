package com.yunjing.sign.api;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.MySignVO;
import com.yunjing.sign.beans.vo.SignDetailVO;
import com.yunjing.sign.beans.vo.SignListVO;
import com.yunjing.sign.excel.BaseExModel;
import com.yunjing.sign.service.ISignConfigDailyService;
import com.yunjing.sign.service.ISignDetailDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * <p>
 * 签到明细 前端控制器
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@RestController
@RequestMapping("/sign/detail-daily/")
public class SignDetailDailyApi extends BaseController {

    @Autowired
    private ISignDetailDailyService iSignDetailDailyService;

    /**
     * 外出签到
     * @param signDetailParam
     * @return
     */
    @PostMapping("/sign")
    public ResponseEntityWrapper toSign(@RequestBody SignDetailParam signDetailParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signDetailParam);
        SignDetailVO isAdd = iSignDetailDailyService.toSign(signDetailParam);
        return success(isAdd);
    }

    /**
     * 获取签到次数
     * @param userId
     * @param orgId
     * @return
     */
    @GetMapping("/get-count")
    public ResponseEntityWrapper getSignCount(@RequestParam String userId, @RequestParam String orgId){
        // 基础校验
        int count = iSignDetailDailyService.getSignCount(userId, orgId);
        return success(count);
    }

    /**
     * 签到统计
     * @param userAndDeptParam
     * @return
     */
    @PostMapping("/sign-count")
    public ResponseEntityWrapper signGroup(@RequestBody UserAndDeptParam userAndDeptParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(userAndDeptParam);
        SignListVO vo = iSignDetailDailyService.getCountInfo(userAndDeptParam);
        return success(vo);
    }

    /**
     * 按月统计我的签到情况
     * @param signDetailParam
     * @return
     */
    @PostMapping("/query-month")
    public ResponseEntityWrapper queryMonthInfo(@RequestBody SignDetailParam signDetailParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signDetailParam);
        MySignVO vo = iSignDetailDailyService.queryMonthInfo(signDetailParam);
        return success(vo);
    }
}
