package com.yunjing.notice.body;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 李双喜
 * @date 2018/3/26 15:07
 */
@Data
public class PushBody implements Serializable{

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
