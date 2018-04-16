package com.yunjing.notice.processor.feign.fallback;

import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.AuthorityFeign;
import org.springframework.stereotype.Component;

/**
 * 发送公告Rpc降级
 *
 * @version 1.0.0
 * @author: 李双喜
 * @date 11/03/2018
 * @description
 **/
@Component
public class AuthorityFallback implements AuthorityFeign {
    /**
     * 是否有权限发送公告
     *
     * @param appId    应用编号
     * @param memberId 企业成员编号
     * @return ResponseEntityWrapper
     */
    @Override
    public ResponseEntityWrapper authority(String appId, Long memberId) {
        return ResponseEntityWrapper.error(StatusCode.HTTP_RESPONSE_ERROR);
    }
}
