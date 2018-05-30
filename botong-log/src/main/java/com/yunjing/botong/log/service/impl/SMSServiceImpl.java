package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.yunjing.botong.log.config.AliYunSmsTransmitter;
import com.yunjing.botong.log.service.ISMSService;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.global.exception.SendMessageFailureException;
import com.yunjing.mommon.validate.ValidateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 短信推送服务
 *
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/3/15 17:03
 * @description
 **/
@Service("smsService")
public class SMSServiceImpl implements ISMSService {

    public final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private AliYunSmsTransmitter aliYunSmsTransmitter;


    @Override
    public void sendSmSMessage(List<String> phoneNumbers, String templateId, String signName, LinkedHashMap<String, String> params) {
        if (!ValidateUtils.isPhone(phoneNumbers)) {
            throw new ParameterErrorException("电话号码参数集合中，存在非法号码");
        }

        logger.info("发送短信输入参数：phoneNumbers" + JSON.toJSONString(phoneNumbers) +
                " templateId:" + templateId + " signName:" + signName + " params：" + JSON.toJSONString(params));

        SendSmsResponse sendSmsResponse = null;
        try {
            if (phoneNumbers.size() == 1) {
                sendSmsResponse = aliYunSmsTransmitter.singleSendSmS(phoneNumbers.get(0), templateId, signName, params);
            } else {
                sendSmsResponse = aliYunSmsTransmitter.patchSendSmS(phoneNumbers, templateId, signName, params);
            }

            final String code = "OK";
            if (sendSmsResponse.getCode() == null || !code.equalsIgnoreCase(sendSmsResponse.getCode())) {
                logger.debug("调用第三方发送消息失败，第三方返回结果为：" + JSONObject.toJSONString(sendSmsResponse));
                throw new SendMessageFailureException(sendSmsResponse.getMessage());
            }
        } catch (ClientException e) {
            logger.error("请求参数: phoneNumbers : " + JSONArray.toJSONString(phoneNumbers) + " templateId：" + templateId
                    + " params: " + JSONObject.toJSONString(params));
            logger.error("调用第三方发送短信消息发生异常，异常原因为：" + e.getMessage(), e);
            throw new SendMessageFailureException(sendSmsResponse != null ? sendSmsResponse.getMessage() : e.getMessage());
        }
    }
}
