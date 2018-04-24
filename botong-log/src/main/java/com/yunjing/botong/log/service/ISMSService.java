package com.yunjing.botong.log.service;

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
public interface ISMSService {


    /**
     * 发送短信
     *
     * @param phoneNumbers 电话号码集合
     * @param templateId   短信模板编号
     * @param signName     短信签名
     * @param params       短信模板参数集合
     * @return
     */
    void sendSmSMessage(List<String> phoneNumbers, String templateId, String signName, LinkedHashMap<String, String> params);
}
