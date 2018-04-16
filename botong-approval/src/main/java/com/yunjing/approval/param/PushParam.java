package com.yunjing.approval.param;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

import java.util.Map;

/**
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/3/16 17:34
 * @description
 **/
@Data
public class PushParam {


    private String appId;

    /** 公司id */
    private String companyId;

    /** 推送标题 */
    @NotNullOrEmpty
    private String notificationTitle;

    /** 自定义标题 */
    @NotNullOrEmpty
    private String title;

    /** 自定义字段（如：avatar 头像  nick 昵称 imTag 账号 jumpType  跳转类型） */
    private Map<String, String> map;

    /** 指定用户 */
    @NotNullOrEmpty
    private String registrationId;

    /** 指定用户集合 */
    private String[] alias;

    @NotNullOrEmpty
    private String msg;

}
