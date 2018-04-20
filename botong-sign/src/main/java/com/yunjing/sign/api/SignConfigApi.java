package com.yunjing.sign.api;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.param.SignConfigParam;
import com.yunjing.sign.beans.vo.SignConfigVO;
import com.yunjing.sign.service.ISignConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;

/**
 * <p>
 * 签到配置 api
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@RestController
@RequestMapping("/sign/out")
public class SignConfigApi extends BaseController {

    @Autowired
    private ISignConfigService iSignConfigService;

    @Value("${botong.log.appId}")
    private String appId;

    /**
     * 设置签到规则
     * @param signConfigParam   签到设置规则对象
     * @return                  返回布尔值，表示是否设置成功
     */
    @PostMapping("/setting")
    public ResponseEntityWrapper setting(@RequestBody SignConfigParam signConfigParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signConfigParam);
        boolean isAdd = iSignConfigService.setSignConfig(signConfigParam);
        return success(isAdd);
    }


    /**
     * 查看签到规则
     * @param orgId     组织Id
     * @return          返回签到设置对象
     */
    @GetMapping("/get-setting")
    public ResponseEntityWrapper getSetting(@RequestParam String orgId){
        // 基础校验
        BeanFieldValidator.getInstance().validate(orgId);
        SignConfigVO vo = iSignConfigService.getSignConfig(orgId);
        return success(vo);
    }

    /**
     * 判断是否具有管理员权限
     * @param memberId  成员id
     * @return          是与否
     */
    @PostMapping("/is-admin")
    public ResponseEntityWrapper isAdmin(@RequestParam String memberId){
        // 基础校验
        boolean isAdd = iSignConfigService.verifyManager(appId, memberId);
        return success(isAdd);
    }

	
}
