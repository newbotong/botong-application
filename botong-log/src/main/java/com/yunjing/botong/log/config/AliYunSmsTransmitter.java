package com.yunjing.botong.log.config;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.yunjing.botong.log.util.SmsUtils;
import com.yunjing.mommon.global.exception.SendMessageFailureException;
import com.yunjing.mommon.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 阿里短信发送器
 *
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/3/15 18:07
 * @description
 **/
@Slf4j
@Component
public class AliYunSmsTransmitter {

    @Value("${ali.sms.accessKey}")
    private String accessKeyId;

    @Value("${ali.sms.secretKey}")
    private String accessKeySecret;

    @Value("${ali.sms.signName}")
    private String signName;

    /**
     * 单个发送短信
     *
     * @param phoneNumbers 电话号码
     * @param templateId   短信模板编号
     * @param signName     短信签名
     * @param params       短信模板参数集合
     * @return
     */
    public SendSmsResponse singleSendSmS(String phoneNumbers, String templateId, String signName, LinkedHashMap<String, String> params)
            throws ClientException {
        String templateParams = JSONObject.toJSONString(params);
        return send(phoneNumbers, templateId, templateParams, signName);
    }

    /**
     * 批量发送短信
     *
     * @param phoneNumbers 电话号码集合
     * @param templateId   短信模板编号
     * @param signName     短信签名
     * @param params       短信模板参数
     * @return
     */
    public SendSmsResponse patchSendSmS(List<String> phoneNumbers, String templateId, String signName, LinkedHashMap<String, String> params)
            throws ClientException {
        //阿里目前批量发送时，高只允许发送1000条
        final int length = 1000;
        final String code = "OK";
        String phoneNumberStr;
        String templateParams = JSONObject.toJSONString(params);
        SendSmsResponse sendSmsResponse = null;

        List<List<String>> list;
        if (phoneNumbers.size() > length) {
            list = ListUtils.getAllowList(phoneNumbers, length);
        } else {
            list = new ArrayList<>();
            list.add(phoneNumbers);
        }

        //分批循环发送
        for (List<String> tempList : list) {
            phoneNumberStr = StringUtils.join(tempList, ",");
            sendSmsResponse = send(phoneNumberStr, templateId, templateParams, signName);
            if (code.equalsIgnoreCase(sendSmsResponse.getCode())) {
                throw new SendMessageFailureException("批量发送消息失败，失败原因为：" + sendSmsResponse.getMessage());
            }
        }

        //如果都成功，则返回最后一次请求结果即可
        return sendSmsResponse;
    }

    /**
     * 阿里云发送模板短信
     *
     * @param phone         电话号码，多个以‘,’分割，最多每次只能发送1000条
     * @param templateCode  短信模板编号
     * @param templateParam 短信模板中的变量替换JSON串
     * @param signName      短信签名
     * @return
     * @throws ClientException
     */
    public SendSmsResponse send(String phone, String templateCode, String templateParam, String signName) throws ClientException {
        String _tempSignName = StringUtils.isBlank(signName) ? this.signName : signName;
        return SmsUtils.send(phone, templateCode, templateParam, accessKeyId, accessKeySecret, _tempSignName);
    }
}
