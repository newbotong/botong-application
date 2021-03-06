package com.yunjing.info.processor.okhttp;

import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.io.IOException;

/**
 * okHttp收藏
 *
 * @author 李双喜
 * @date 2018/4/8 16:17
 */
public interface CollectService {

    /**
     * okHttp查询是否收藏
     *
     * @param userId   用户id
     * @param originId 资讯内容id
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @GET("/api/microapps/appcenter/user/verification")
    Call<ResponseEntityWrapper> collectState(@Query("userId") String userId, @Query("originId") String originId) throws BaseException,IOException;
}
