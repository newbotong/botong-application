package com.yunjing.sign.processor.feign;

import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.vo.SignUserInfoVO;
import com.yunjing.sign.processor.feign.fallback.UserRemoteServiceFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 11/03/2018
 * @description
 **/
@FeignClient(name = "botong-org-structure", fallback = UserRemoteServiceFallback.class)
public interface UserRemoteService {

    /**
     * 获取所有的人员id
     * @param deptIds
     * @param memberIds
     * @return
     */
    @PostMapping("/rpc/member/find-sub-lists")
    ResponseEntityWrapper<List<SignUserInfoVO>> findSubLists(@RequestParam(value = "deptIds", required = false) String[] deptIds, @RequestParam(value = "memberIds", required = false) String[] memberIds);

    /**
     * 分页获取人员id
     * @param deptIds
     * @param memberIds
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/rpc/member/find-member-page")
    ResponseEntityWrapper<PageWrapper<SignUserInfoVO>> findMemberPage(@RequestParam(value = "deptIds", required = false) String[] deptIds, @RequestParam(value = "memberIds", required = false) String[] memberIds,
                                                                      @RequestParam(value = "pageNo") int pageNo, @RequestParam(value = "pageSize") int pageSize);
}
