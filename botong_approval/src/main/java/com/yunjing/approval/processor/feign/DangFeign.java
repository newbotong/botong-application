package com.yunjing.approval.processor.feign;

import com.yunjing.approval.param.DangParam;
import com.yunjing.approval.processor.feign.fallback.DangFallback;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 刘小鹏
 * @date 2018/3/21 16:45
 */
//@FeignClient(name = "botong-dang", fallback = DangFallback.class)
public interface DangFeign {
    /**
     * 发送Dang
     *
     * @param param dang参数
     * @return
     */
//    @PostMapping("/rpc/dang/send")
//    ResponseEntityWrapper sendDang(@RequestBody DangParam param);


}
