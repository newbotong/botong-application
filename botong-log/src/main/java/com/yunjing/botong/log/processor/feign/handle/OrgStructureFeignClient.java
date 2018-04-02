package com.yunjing.botong.log.processor.feign.handle;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * <p> 组织架构rpc
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@FeignClient("botong-org-structure")
public interface OrgStructureFeignClient {

    /**
     * 检查成员是否为某应用的管理员
     *
     * @param memberId
     * @param appId
     * @return
     */
    @PostMapping("/rpc/org/admin/is-manager")
    ResponseEntityWrapper isManager(@RequestParam("memberId") long memberId, @RequestParam("appId") String appId);


    /**
     * 获取企业管理员所管辖的企业成员
     *
     * @param memberId
     * @param appId
     * @return
     */
    @GetMapping("/rpc/org/admin/manage/scope")
    ResponseEntityWrapper scope(@RequestParam("memberId") long memberId, @RequestParam("appId") String appId);

    /**
     * 获取成员信息列表（支持批量）
     *
     * @param memberIds
     * @return
     */
    @PostMapping("/rpc/member/find-member-list")
    ResponseEntityWrapper findMemberInfo(@RequestParam("memberIds") String memberIds);
}
