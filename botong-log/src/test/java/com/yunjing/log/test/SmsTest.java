package com.yunjing.log.test;

import com.yunjing.botong.log.LogApplication;
import com.yunjing.botong.log.service.ISMSService;
import com.yunjing.mommon.base.SmSParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * <p> 短信模版
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LogApplication.class)
public class SmsTest {

    @Autowired
    private ISMSService smsService;

    @Test
    public void test() {
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("18192575006");
        // sms
        SmSParam param = new SmSParam();
        param.setPhoneNumbers(phoneNumbers);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //发送的内容
        map.put("message", "当日的日报");
        param.setMapParam(map);
        param.setSignName("伯通");
        param.setTemplateId("SMS_130830537");
        smsService.sendSmSMessage(phoneNumbers, param.getTemplateId(), param.getSignName(), param.getMapParam());
    }
}
