package com.yunjing.approval.processor.feign;

import com.yunjing.approval.processor.feign.fallback.OrgUserFallback;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 刘小鹏
 * @date 2018/3/26
 */
//@FeignClient(name = "botong-org-structure", fallback = OrgUserFallback.class)
public interface OrgUserFeign {
    /**
     * 获取企业用户信息
     *
     * @param orgId 企业主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/getOrgUser")
    ResponseEntityWrapper getOrgUser(@RequestParam Long orgId);
}
