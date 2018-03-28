package com.yunjing.notice.processor.feign;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.fallback.AuthorityFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李双喜
 * @date 2018/3/21 11:37
 */
@FeignClient(name = "botong-org-admin", fallback = AuthorityFallback.class)
public interface AuthorityFeign {
    /**
     * 是否有权限发送公告
     *
     * @param appId    应用编号
     * @param memberId 企业成员编号
     * @return ResponseEntityWrapper
     */
    @RequestMapping("/rpc/org/admin/isManager")
    ResponseEntityWrapper authority(@RequestParam("appId") String appId, @RequestParam("memberId") Long memberId);
}
