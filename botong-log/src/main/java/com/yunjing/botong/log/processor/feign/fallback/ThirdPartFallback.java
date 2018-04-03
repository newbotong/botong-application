package com.yunjing.botong.log.processor.feign.fallback;

import com.yunjing.botong.log.processor.feign.handle.ThirdPartFeignClient;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.base.SmSParam;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 降级处理
 * <p>
 * <p>使用okhttp调用
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@Component
@Deprecated
public class ThirdPartFallback implements ThirdPartFeignClient {
    /**
     * 推送至指定用户
     *
     * @param pushParam
     * @return
     */
    @Override
    public ResponseEntityWrapper sendByAlias(PushParam pushParam) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }

    /**
     * 发送短信
     *
     * @param smSParam
     * @return
     */
    @Override
    public ResponseEntityWrapper sendSms(SmSParam smSParam) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }
}

