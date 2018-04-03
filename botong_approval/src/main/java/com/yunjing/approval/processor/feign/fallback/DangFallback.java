package com.yunjing.approval.processor.feign.fallback;

import com.yunjing.approval.param.DangParam;
import com.yunjing.approval.processor.feign.DangFeign;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.stereotype.Component;

/**
 * @author 刘小鹏
 * @date 2018/3/21 16:46
 */
@Component
public class DangFallback implements DangFeign {

    /**
     * 发送Dang
     *
     * @param dangParam dang参数
     * @return
     */
//    @Override
//    public ResponseEntityWrapper sendDang(DangParam dangParam) {
//        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
//    }
}
