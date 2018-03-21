package com.yunjing.notice.feign;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李双喜
 * @date 2018/3/21 11:37
 */
@Component
@FeignClient(name = "botong-admin")
public interface AuthorityFeign {
    /**
     * 是否有权限发送公告
     * @param appId       公告应用id
     * @param issueUserId 发布人的用户id
     * @return            ResponseEntityWrapper
     */
    @PostMapping("")
    ResponseEntityWrapper authority(@RequestParam String appId,@RequestParam String issueUserId);
}
