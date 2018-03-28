package com.yunjing.sign.processor.feign.fallback;

import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.mommon.wrapper.RpcResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 11/03/2018
 * @description
 **/
@Component
public class UserRemoteServiceFallback {

    public ResponseEntityWrapper findSubLists(String[] deptIds, String[] memberIds) {
        return ResponseEntityWrapper.error(StatusCode.FORBIDDEN);
    }

    public ResponseEntityWrapper findMemberPage(String[] deptIds, String[] memberIds, int pageNo, int pageSize) {
        return ResponseEntityWrapper.error(StatusCode.FORBIDDEN);
    }
}
