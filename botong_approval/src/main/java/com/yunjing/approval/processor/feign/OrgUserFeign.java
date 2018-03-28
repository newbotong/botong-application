package com.yunjing.approval.processor.feign;

import com.yunjing.approval.processor.feign.fallback.OrgUserFallback;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.POST;

/**
 * @author 刘小鹏
 * @date 2018/3/26
 */
@FeignClient(name = "botong-org-structure", fallback = OrgUserFallback.class)
public interface OrgUserFeign {
    /**
     * 获取企业用户信息
     *
     * @param companyId 企业主键
     * @param passportIds 账号主键
     * @return ResponseEntityWrapper
     */
    @RequestMapping(value = "/rpc/member/get-members-list",method = RequestMethod.POST)
    ResponseEntityWrapper getMemberList(@RequestParam String companyId,@RequestParam String passportIds);
}
