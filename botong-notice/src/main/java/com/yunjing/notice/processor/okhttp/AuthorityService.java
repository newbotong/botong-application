package com.yunjing.notice.processor.okhttp;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.fallback.AuthorityFallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * okhttp调用权限验证
 *
 * @author tandk
 * @date 2018/4/4 11:47
 */
public interface AuthorityService {

    /**
     * 是否有权限发送公告
     *
     * @param appId    应用编号
     * @param memberId 企业成员编号
     * @return Call<ResponseEntityWrapper>
     */

    @GET("/rpc/org/admin/is-manager")
    Call<ResponseEntityWrapper> authority(@Query("appId") String appId, @Query("memberId") String memberId);
}
