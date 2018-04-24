package com.yunjing.botong.log.params;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 短信发送参数
 *
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/3/16 10:35
 * @description
 **/
@Data
public class SmSParam {

    /**
     * 手机号码集合
     */
    private List<String> phoneNumbers;

    /**
     * 短信模板编号
     */
    @NotNullOrEmpty
    private String templateId;

    /**
     * 短信模板签名
     */
    private String signName;

    /**
     * 短信模板参数集合
     */
    private LinkedHashMap<String, String> mapParam;
}
