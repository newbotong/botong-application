package com.yunjing.notice.processor.feign.fallback;

import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.DangFeign;
import org.springframework.stereotype.Component;

/**
 * @author 李双喜
 * @date 2018/3/21 16:46
 */
@Component
public class DangFallback implements DangFeign {

    /**
     * 发送Dang
     *
     * @param userId          用户id，发送者
     * @param bizType         业务类型(0 Dang 1 公告)
     * @param bizId           业务ID
     * @param receiveBody     接收人信息对象
     * @param dangType        dang类型(1.文字 2.语音)
     * @param remindType      dang提箱类型(1.应用内 2.短信 )
     * @param sendType        发送类型(1,立即；2定时)
     * @param sendTime        发送时间（时间戳）
     * @param sendContent     发送内容 或者 语音地址
     * @param voiceTimeLength 语音时长
     * @param sendTelephone   发送者手机号
     * @param isAccessory     是否有附件(0没有；1有)
     * @param accessoryType   附件类型(1.图片 2.视频 3.链接 )
     * @param accessoryUrl    附件地址
     * @param accessorySize   附件大小
     * @return
     */
    @Override
    public ResponseEntityWrapper sendDang(Long userId, Integer bizType, Long bizId, String receiveBody, Integer dangType, Integer remindType, Integer sendType, Long sendTime, String sendContent, Integer voiceTimeLength, Long sendTelephone, Integer isAccessory, Integer accessoryType,String accessoryName, String accessoryUrl, String accessorySize) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }
}
