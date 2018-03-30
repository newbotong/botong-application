package com.yunjing.botong.log.processor.feign.handle;

import com.yunjing.botong.log.processor.feign.fallback.ThirdPartFallback;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.base.SmSParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * <p> 第三方服务 rpc
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@FeignClient(value = "botong-third-party", fallback = ThirdPartFallback.class)
public interface ThirdPartFeignClient {

    /**
     * 推送至指定用户
     *
     * @param pushParam
     * @return
     */
    @RequestMapping(value = "/rpc/push/sendByAlias")
    ResponseEntityWrapper sendByAlias(@RequestBody PushParam pushParam);

    /**
     * 发送短信
     *
     * @param smSParam
     * @return
     */
    @RequestMapping(value = "/rpc/sms/sendSMS")
    ResponseEntityWrapper sendSms(@RequestBody SmSParam smSParam);
}
