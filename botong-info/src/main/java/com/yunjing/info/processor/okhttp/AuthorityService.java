package com.yunjing.info.processor.okhttp;

import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;

/**
 * okhttp调用权限验证
 *
 * @author tandk
 * @date 2018/4/4 11:47
 */
public interface AuthorityService {

    /**
     * 是否有权限发送资讯
     *
     * @param appId    应用编号
     * @param memberId 企业成员编号
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @GET("/rpc/org/admin/is-manager")
    Call<ResponseEntityWrapper> authority(@Query("appId") String appId, @Query("memberId") Long memberId) throws BaseException, IOException;
}
