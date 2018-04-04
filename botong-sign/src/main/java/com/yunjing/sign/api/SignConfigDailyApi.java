package com.yunjing.sign.api;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.param.SignConfigParam;
import com.yunjing.sign.beans.vo.SignConfigVO;
import com.yunjing.sign.service.ISignConfigDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@RestController
@RequestMapping("/sign/daily")
public class SignConfigDailyApi extends BaseController {

    @Autowired
    private ISignConfigDailyService iSignConfigDailyService;

    /**
     * 设置签到规则
     * @param signConfigParam   签到设置规则对象
     * @return                  返回布尔值，表示是否设置成功
     */
    @PostMapping("/setting")
    public ResponseEntityWrapper setting(@RequestBody SignConfigParam signConfigParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signConfigParam);
        boolean isAdd = iSignConfigDailyService.setSignConfig(signConfigParam);
        return success(isAdd);
    }


    /**
     * 查看签到规则
     * @param orgId     组织Id
     * @return
     */
    @GetMapping("/get-setting")
    public ResponseEntityWrapper getSetting(@RequestParam String orgId){
        // 基础校验
        BeanFieldValidator.getInstance().validate(orgId);
        SignConfigVO vo = iSignConfigDailyService.getSignConfig(orgId);
        return success(vo);
    }
}
