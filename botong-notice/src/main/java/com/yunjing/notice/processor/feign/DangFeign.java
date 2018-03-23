package com.yunjing.notice.processor.feign;

import com.yunjing.notice.processor.feign.fallback.DangFallback;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author 李双喜
 * @date 2018/3/21 16:45
 */
@FeignClient(name = "botong-admin",fallback = DangFallback.class)
public interface DangFeign {
    /**
     * 添加Dang
     */
}
