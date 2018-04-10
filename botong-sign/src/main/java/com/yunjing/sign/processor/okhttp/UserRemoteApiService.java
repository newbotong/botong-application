package com.yunjing.sign.processor.okhttp;

import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.vo.SignUserInfoVO;
import com.yunjing.sign.processor.feign.fallback.UserRemoteServiceFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Call;
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
    @POST("/appcenter/org/find-sub-lists")
    Call<ResponseEntityWrapper<List<SignUserInfoVO>>> findSubLists(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds);

    /**
     * 分页获取人员id
     * @param deptIds
     * @param memberIds
     * @param pageNo
     * @param pageSize
     * @return
     */
    @POST("/appcenter/org/find-member-page")
    Call<ResponseEntityWrapper<PageWrapper<SignUserInfoVO>>> findMemberPage(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds,
                                                                            @Query("pageNo") int pageNo, @Query("pageSize")int pageSize);
}
