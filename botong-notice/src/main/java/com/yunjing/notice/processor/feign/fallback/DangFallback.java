package com.yunjing.notice.processor.feign.fallback;

import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.DangFeign;
import com.yunjing.notice.processor.feign.param.DangParam;
import org.springframework.stereotype.Component;

/**
 * @author 李双喜
 * @date 2018/3/21 16:46
 */
@Component
public class DangFallback implements DangFeign {

    /**
     * 发送Dang
     * dangParam 发送DANG入参
     * @return
     */
    @Override
    public ResponseEntityWrapper sendDang(DangParam dangParam) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }
}
