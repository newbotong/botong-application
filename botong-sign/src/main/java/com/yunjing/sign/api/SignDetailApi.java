package com.yunjing.sign.api;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.MySignVO;
import com.yunjing.sign.beans.vo.SignDetailVO;
import com.yunjing.sign.beans.vo.SignListVO;
import com.yunjing.sign.dao.mapper.SignDetailDailyMapper;
import com.yunjing.sign.dao.mapper.SignDetailMapper;
import com.yunjing.sign.excel.BaseExModel;
import com.yunjing.sign.service.ISignDetailService;
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
@RequestMapping("/sign/detail-out/")
public class SignDetailApi extends BaseController {

    @Autowired
    private ISignDetailService iSignDetailService;

    @Autowired
    private SignDetailMapper signDetailMapper;


    

    /**
     * 外出签到
     * @param signDetailParam       签到明细参数
     * @return                      返回签到明细对象
     */
    @PostMapping("/sign")
    public ResponseEntityWrapper toSign(@RequestBody SignDetailParam signDetailParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signDetailParam);
        SignDetailVO isAdd = iSignDetailService.toSign(signDetailParam);
        return success(isAdd);
    }

    /**
     * 获取签到次数
     * @param userId            memberId
     * @param orgId             企业id
     * @return                  返回签到次数
     */
    @GetMapping("/get-count")
    public ResponseEntityWrapper getSignCount(@RequestParam String userId, @RequestParam String orgId){
        // 基础校验
        int count = iSignDetailService.getSignCount(userId, orgId);
        return success(count);
    }

    /**
     * 签到统计
     * @param userAndDeptParam  多部门Id和多memberId
     * @return                  返回未签到和已签到列表
     */
    @PostMapping("/sign-count")
    public ResponseEntityWrapper signGroup(@RequestBody UserAndDeptParam userAndDeptParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(userAndDeptParam);
        SignListVO vo = iSignDetailService.getCountInfo(userAndDeptParam, signDetailMapper);
        return success(vo);
    }

    /**
     * 按月统计我的签到情况
     * @param signDetailParam    签到明细参数
     * @return                   返回用户一月的签到详情
     */
    @PostMapping("/query-month")
    public ResponseEntityWrapper queryMonthInfo(@RequestBody SignDetailParam signDetailParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signDetailParam);
        MySignVO vo = iSignDetailService.queryMonthInfo(signDetailParam, signDetailMapper);
        return success(vo);
    }


	
}
