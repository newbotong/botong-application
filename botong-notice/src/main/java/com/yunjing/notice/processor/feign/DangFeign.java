package com.yunjing.notice.processor.feign;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.fallback.DangFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李双喜
 * @date 2018/3/21 16:45
 */
@FeignClient(name = "botong-dang", fallback = DangFallback.class)
public interface DangFeign {
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
    @PostMapping("/rpc/dang/send")
    ResponseEntityWrapper sendDang(@RequestParam("userId") Long userId, @RequestParam("bizType") Integer bizType,
                                   @RequestParam("bizId") Long bizId, @RequestParam("receiveBody") String receiveBody,
                                   @RequestParam("dangType") Integer dangType, @RequestParam("remindType") Integer remindType,
                                   @RequestParam("sendType") Integer sendType, @RequestParam(value = "sendTime",required = false)Long sendTime,
                                   @RequestParam(value = "sendContent",required = false) String sendContent,
                                   @RequestParam(value = "voiceTimeLength",required = false) Integer voiceTimeLength,
                                   @RequestParam(value = "sendTelephone",required = false) Long sendTelephone,
                                   @RequestParam("isAccessory") Integer isAccessory,
                                   @RequestParam(value = "accessoryType",required = false) Integer accessoryType,
                                   @RequestParam(value = "accessoryName",required = false) String accessoryName,
                                   @RequestParam(value = "accessoryUrl",required = false) String accessoryUrl,
                                   @RequestParam(value = "accessorySize",required = false) String accessorySize);


}
