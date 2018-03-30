package com.yunjing.botong.log.processor.feign.fallback;

import com.yunjing.botong.log.processor.feign.handle.DangFeignClient;
import com.yunjing.botong.log.processor.feign.param.DangParam;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 降级处理
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@Component
public class DangFallback implements DangFeignClient {

    @Override
    public ResponseEntityWrapper send(DangParam param) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }
}
