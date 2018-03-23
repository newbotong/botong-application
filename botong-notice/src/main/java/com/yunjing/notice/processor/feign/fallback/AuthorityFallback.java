package com.yunjing.notice.processor.feign.fallback;

import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.AuthorityFeign;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: 李双喜
 * @date 11/03/2018
 * @description
 **/
@Component
public class AuthorityFallback implements AuthorityFeign{

    @Override
    public ResponseEntityWrapper authority(Long appId, Long issueUserId) {
        return ResponseEntityWrapper.error(StatusCode.INTERNET_IO_ERROR);
    }
}
