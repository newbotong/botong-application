package com.yunjing.notice.processor.okhttp;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.param.DangParam;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * okhttp调用应用中心dang服务
 *
 * @author tandk
 * @date 2018/4/4 14:04
 */
public interface DangService {
    /**
     * 发送Dang
     *
     * @param dangParam dang数据
     * @return Call<ResponseEntityWrapper>
     */

    @POST("/api/microapps/appcenter/dang/send-dang")
    Call<ResponseEntityWrapper> sendDang(@Body DangParam dangParam);

}
