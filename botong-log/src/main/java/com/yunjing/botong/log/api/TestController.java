package com.yunjing.botong.log.api;

import com.yunjing.botong.log.http.AppCenterService;
import com.yunjing.botong.log.processor.feign.param.DangParam;
import com.yunjing.botong.log.processor.feign.param.UserInfoModel;
import com.yunjing.mommon.base.PushParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/30.
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AppCenterService appCenterService;

    @RequestMapping("/push")
    public String push() {
        // 创建请求参数
        PushParam param = new PushParam();
        param.setTitle("Title");
        param.setNotificationTitle("NotificationTitle");
        param.setAlias(new String[]{"6386505037916409856"});
        appCenterService.push(param);
        return "success";
    }

    @RequestMapping("/dang")
    public String dang() {
        DangParam param = new DangParam();
        param.setIsAccessory(0);
        param.setSendTelephone(18562818246L);
        param.setUserId(6386505037916409856L);
        List<UserInfoModel> infoModels = new ArrayList<>();
        infoModels.add(new UserInfoModel(18562818246L, 6386505037916409856L));
        param.setReceiveBody(infoModels);
        param.setDangType(1);
        param.setRemindType(1);
        param.setSendType(1);
        param.setSendContent("dang消息内容");
        appCenterService.dang(param);
        return "success";
    }
}
