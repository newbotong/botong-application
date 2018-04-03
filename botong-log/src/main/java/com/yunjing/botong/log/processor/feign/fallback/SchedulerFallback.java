package com.yunjing.botong.log.processor.feign.fallback;

import com.yunjing.botong.log.processor.feign.handle.SchedulerFeignClient;
import com.yunjing.botong.log.processor.feign.param.SchedulerParam;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 任务调度降级处理
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@Component
public class SchedulerFallback implements SchedulerFeignClient {

    @Override
    public ResponseEntityWrapper set(SchedulerParam param) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }

    @Override
    public ResponseEntityWrapper cancel(Long taskId, String jobTitle) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }

}
