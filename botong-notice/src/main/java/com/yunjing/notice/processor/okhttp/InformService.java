package com.yunjing.notice.processor.okhttp;

import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * okhttp调用第三方服务
 *
 * @author tandk
 * @date 2018/4/4 14:04
 */
public interface InformService {
    /**
     * 公告推送(工作通知)
     *
     * @param pushParam 入参
     * @return
     */
    @POST("/rpc/push/push-all-target-user")
    Call<ResponseEntityWrapper> pushAllTargetByUser(@Body PushParam pushParam);
}
