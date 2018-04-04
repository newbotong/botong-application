package com.yunjing.notice.processor.okhttp;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.fallback.DangFallback;
import com.yunjing.notice.processor.feign.param.DangParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
