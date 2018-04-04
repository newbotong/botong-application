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
     * 审批推送
     *
     * @param pushParam 推送参数
     * @return
     */
    @PostMapping("/rpc/push/push-all-target-user")
    ResponseEntityWrapper pushAllTargetByUser(@RequestBody PushParam pushParam);
}
