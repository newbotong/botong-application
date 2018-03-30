package com.yunjing.botong.log.processor.feign.handle;

import com.yunjing.botong.log.processor.feign.fallback.DangFallback;
import com.yunjing.botong.log.processor.feign.param.DangParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * <p> dang服务 rpc
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@FeignClient(value = "botong-dang", fallback = DangFallback.class)
public interface DangFeignClient {


    /**
     * 发送dang消息
     *
     * @param param
     * @return
     */
    @PostMapping("/rpc/dang/send")
    ResponseEntityWrapper send(@RequestBody DangParam param);

}
