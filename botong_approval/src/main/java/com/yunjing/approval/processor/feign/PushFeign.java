package com.yunjing.approval.processor.feign;

import com.yunjing.approval.param.PushParam;
import com.yunjing.approval.processor.feign.fallback.PushFallback;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 刘小鹏
 * @date 2018/3/21 16:48
 */
@FeignClient(name = "botong-third-party", fallback = PushFallback.class)
public interface PushFeign {
    /**
     * 公告推送(工作通知)
     *
     * @param pushParam 入参
     * @return
     */
    @PostMapping("/rpc/push/push-target-user")
    ResponseEntityWrapper pushAllTargetByUser(@RequestBody PushParam pushParam);
}
