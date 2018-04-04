package com.yunjing.notice.processor.feign.fallback;

import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.PushBody;
import com.yunjing.notice.processor.feign.InformFeign;
import org.springframework.stereotype.Component;

/**
 * 用户信息降级
 *
 * @author 李双喜
 * @date 2018/3/21 16:48
 */
@Component
public class InformFallback implements InformFeign {
    /**
     * 公告推送(工作通知)
     *
     * @param pushParam 入参
     * @return
     */
    @Override
    public ResponseEntityWrapper pushAllTargetByUser(PushBody pushParam) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }
}
