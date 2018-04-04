package com.yunjing.approval.param;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import com.yunjing.mommon.validate.annotation.Size;
import lombok.Data;

/**
 * Dang对象参数
 *
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/3/29 10:39
 * @description
 **/
@Data
public class DangParam {

    /**
     * 用户id，发送者
     */
    @NotNullOrEmpty(message = "发送者编号不能为空")
    private Long userId;

    /**
     * 业务类型(0 Dang 1 公告)
     */
    @Size(max = 1, message = "Dang 业务类型错误")
    private int bizType;

    /**
     * 业务ID
     */
    @NotNullOrEmpty(message = "业务编号不能为空")
    private Long bizId;

    /**
     * 接收人信息对象
     */
    @NotNullOrEmpty(message = "接收者对象不能为空")
    private String receiveBody;

    /**
     * dang类型(1.文字 2.语音)
     */
    @Size(max = 2, min = 1, message = "Dang类型错误")
    private int dangType;

    /**
     * dang提醒类型(1.应用内 2.短信 )
     */
    @Size(max = 2, min = 1, message = "Dang提醒类型错误")
    private int remindType;

    /**
     * 发送类型(1,立即；2定时)
     */
    @Size(max = 2, min = 1, message = "Dang发送类型错误")
    private int sendType;

    /**
     * 发送时间（时间戳）
     */
    private Long sendTime;

    /**
     * 发送内容 或者 语音地址
     */
    private String sendContent;

    /**
     * 语音时长
     */
    private Integer voiceTimeLength;

    /**
     * 发送者手机号
     */
    private Long sendTelephone;

    /**
     * 是否有附件(0没有；1有)
     */
    private Integer isAccessory;

    /**
     * 附件类型(1.图片 2.视频 3.链接 )
     */
    private Integer accessoryType;

    /**
     * 附件名称
     */
    private String accessoryName;

    /**
     * 附件地址
     */
    private String accessoryUrl;

    /**
     * 附件大小
     */
    private String accessorySize;

}
