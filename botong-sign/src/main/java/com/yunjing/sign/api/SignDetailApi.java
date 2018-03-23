package com.yunjing.sign.api;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.MySignVO;
import com.yunjing.sign.beans.vo.SignListVO;
import com.yunjing.sign.service.ISignDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 签到明细 前端控制器
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@RestController
@RequestMapping("/api/signDetail/out/")
public class SignDetailApi extends BaseController {

    @Autowired
    private ISignDetailService iSignDetailService;

    /**
     * 外出签到
     * @param signDetailParam
     * @return
     */
    @PostMapping("/toSign")
    public ResponseEntityWrapper toSign(@RequestBody SignDetailParam signDetailParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signDetailParam);
        boolean isAdd = iSignDetailService.toSign(signDetailParam);
        return success(isAdd);
    }

    /**
     * 获取签到次数
     * @param userId
     * @param orgId
     * @return
     */
    @GetMapping("/getSignCount")
    public ResponseEntityWrapper getSignCount(@RequestParam String userId, @RequestParam String orgId){
        // 基础校验
        int count = iSignDetailService.getSignCount(userId, orgId);
        return success(count);
    }

    /**
     * 签到统计
     * @param userAndDeptParam
     * @return
     */
    @PostMapping("/signCount")
    public ResponseEntityWrapper signGroup(@RequestBody UserAndDeptParam userAndDeptParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(userAndDeptParam);
        SignListVO vo = iSignDetailService.getCountInfo(userAndDeptParam);
        return success(vo);
    }

    /**
     * 按月统计我的签到情况
     * @param signDetailParam
     * @return
     */
    @PostMapping("/queryMonthInfo")
    public ResponseEntityWrapper queryMonthInfo(@RequestBody SignDetailParam signDetailParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signDetailParam);
        MySignVO vo = iSignDetailService.queryMonthInfo(signDetailParam);
        return success(vo);
    }
	
}
