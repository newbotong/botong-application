package com.yunjing.approval.processor.feign.fallback;

import com.yunjing.approval.processor.feign.OrgUserFeign;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: 李双喜
 * @date 11/03/2018
 * @description
 **/
@Component
public class OrgUserFallback implements OrgUserFeign {

    @Override
    public ResponseEntityWrapper getMemberList(String companyId,String passportIds) {
        return ResponseEntityWrapper.error(StatusCode.INTERNET_IO_ERROR);
    }
}
