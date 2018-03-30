package com.yunjing.botong.log.processor.feign.handle;

import com.yunjing.botong.log.processor.feign.fallback.SchedulerFallback;
import com.yunjing.botong.log.processor.feign.param.SchedulerParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * <p> 任务调度rpc
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@FeignClient(value = "botong-scheduler", fallback = SchedulerFallback.class)
public interface SchedulerFeignClient {

    /**
     * 设置任务
     *
     * @param param
     * @return
     */
    @PostMapping("/rpc/scheduler/set")
    ResponseEntityWrapper set(@RequestBody SchedulerParam param);

    /**
     * 取消任务
     *
     * @param param
     * @return
     */
    @PostMapping("/rpc/scheduler/cancel")
    ResponseEntityWrapper cancel(@RequestBody SchedulerParam param);
}
