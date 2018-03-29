package com.yunjing.notice.processor.feign;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.PushBody;
import com.yunjing.notice.processor.feign.fallback.DangFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李双喜
 * @date 2018/3/21 16:48
 */
@FeignClient(name = "botong-third-party",fallback = DangFallback.class)
public interface InformFeign {
    /**
     * 公告推送(工作通知)
     * @param pushParam  入参
     * @return
     */
    @PostMapping("/rpc/push/push-all-target-user")
    ResponseEntityWrapper pushAllTargetByUser(@RequestBody PushBody pushParam);
}
