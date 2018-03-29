package com.yunjing.notice.processor.feign;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.fallback.DangFallback;
import com.yunjing.notice.processor.feign.param.DangParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李双喜
 * @date 2018/3/21 16:45
 */
@FeignClient(name = "botong-dang", fallback = DangFallback.class)
public interface DangFeign {

    /**
     * 发送Dang
     * @return
     */
    @PostMapping("/rpc/dang/send")
    ResponseEntityWrapper sendDang(@RequestBody DangParam dangParam);


}
