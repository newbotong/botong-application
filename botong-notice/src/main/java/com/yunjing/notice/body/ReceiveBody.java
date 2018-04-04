package com.yunjing.notice.body;

import lombok.Data;

import java.io.Serializable;

/**
 * MQ接收返回参数
 *
 * @author 李双喜
 * @date 2018/3/26 11:06
 */
@Data
public class ReceiveBody implements Serializable {
    /**
     * userId 用户id
     */
    private Long userId;
    /**
     * 用户手机号码
     */
    private Long userTelephone;
}
