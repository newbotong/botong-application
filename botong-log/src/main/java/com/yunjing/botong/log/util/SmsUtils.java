package com.yunjing.botong.log.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * 阿里云短信推送类
 *
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/3/16 16:23
 * @description
 **/
public class SmsUtils {

    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    private final static String product = "Dysmsapi";

    /**
     * 产品域名,开发者无需替换
     */
    private final static String domain = "dysmsapi.aliyuncs.com";


    /**
     * 阿里云发送模板短信
     *
     * @param phone           电话号码，多个以‘,’分割，最多每次只能发送1000条
     * @param templateCode    短信模板编号
     * @param templateParam   短信模板中的变量替换JSON串
     * @param accessKeyId     accessKey
     * @param accessKeySecret accessKey
     * @param signName        sign
     * @return
     * @throws ClientException
     */
    public static SendSmsResponse send(String phone, String templateCode,
                                       String templateParam, String accessKeyId, String accessKeySecret, String signName)
            throws ClientException {
        //超时时间(可自助调整)
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码
        request.setPhoneNumbers(phone);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam(templateParam);

        return acsClient.getAcsResponse(request);
    }

}
