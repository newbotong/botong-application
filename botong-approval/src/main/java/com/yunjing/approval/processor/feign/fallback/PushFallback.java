package com.yunjing.approval.processor.feign.fallback;

import com.yunjing.approval.param.PushParam;
import com.yunjing.approval.processor.feign.PushFeign;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.stereotype.Component;

/**
 * @author 刘小鹏
 * @date 2018/3/21 16:48
 */
@Component
public class PushFallback implements PushFeign {
    /**
     * 审批推送
     *
     * @param pushParam 入参
     * @return
     */
    @Override
    public ResponseEntityWrapper pushAllTargetByUser(PushParam pushParam) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }
}
