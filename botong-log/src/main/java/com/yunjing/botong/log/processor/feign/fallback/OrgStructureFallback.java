package com.yunjing.botong.log.processor.feign.fallback;

import com.yunjing.botong.log.processor.feign.handle.OrgStructureFeignClient;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> 组织结构rpc降级处理
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@Component
public class OrgStructureFallback implements OrgStructureFeignClient {

    @Override
    public ResponseEntityWrapper isManager(long memberId, String appId) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }
}
