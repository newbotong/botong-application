package com.yunjing.notice.processor.feign.fallback;

import com.yunjing.notice.processor.feign.DangFeign;
import org.springframework.stereotype.Component;

/**
 * @author 李双喜
 * @date 2018/3/21 16:46
 */
@Component
public class DangFallback implements DangFeign {
//    @Override
//    public ResponseEntityWrapper authority(Long appId, Long issueUserId) {
//        return ResponseEntityWrapper.error(StatusCode.INTERNET_IO_ERROR);
//    }
}
