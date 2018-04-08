package com.yunjing.approval.processor.okhttp;

import com.yunjing.approval.param.DangParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * okhttp调用dang服务
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
    @POST("/rpc/dang/send")
    Call<ResponseEntityWrapper> sendDang(@Body DangParam dangParam);

}
