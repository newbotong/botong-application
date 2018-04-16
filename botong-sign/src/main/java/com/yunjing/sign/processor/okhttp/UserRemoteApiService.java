package com.yunjing.sign.processor.okhttp;

import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.vo.SignUserInfoVO;
import com.yunjing.sign.processor.feign.fallback.UserRemoteServiceFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 11/03/2018
 * @description
 **/
public interface UserRemoteApiService {

    /**
     * 获取所有的人员id
     * @param deptIds
     * @param memberIds
     * @return
     */
    List<SignUserInfoVO> findSubLists(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds);

    /**
     * 分页获取人员id
     * @param deptIds
     * @param memberIds
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageWrapper<SignUserInfoVO> findMemberPage(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds,
                                                                            @Query("pageNo") int pageNo, @Query("pageSize")int pageSize);

    /**
     * 获取企业成员管理范围
     *
     * @param appId
     * @param memberId
     * @return
     */
    List<SignUserInfoVO> manageScope(@Query("appId") String appId, @Query("memberId") String memberId);

    /**
     * 校验用户权限
     *
     * @param appId
     * @param memberId
     * @return
     */
    boolean verifyManager(String appId, String memberId);
}
