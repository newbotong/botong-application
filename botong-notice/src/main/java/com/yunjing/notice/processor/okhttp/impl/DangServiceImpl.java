package com.yunjing.notice.processor.okhttp.impl;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.feign.param.DangParam;
import com.yunjing.notice.processor.okhttp.DangService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * okhttp调用dang服务
 * @author 谈东魁
 * @date 2018/4/3 15:58
 */
@Slf4j
@Service
public class DangServiceImpl implements DangService {

    @Value("${okhttp.botong-dang}")
    String BASE_URL;

    private DangService service;

    private void initRetrofit(){
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 构建请求对象
        service = retrofit.create(DangService.class);
    }

    /**
     * 发送dang消息
     *
     * @param dangParam    当消息
     * @return ResponseEntityWrapper
     */
    @Override
    public Call<ResponseEntityWrapper> sendDang(DangParam dangParam) {
        initRetrofit();
        try {
            Call<ResponseEntityWrapper> call = service.sendDang(dangParam);
            call.execute();
            return call.clone();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

        //异步请求
//        authorityService.authority(appId, memberId).enqueue(new Callback<ResponseEntityWrapper>() {
//            @Override
//            public void onResponse(Call<ResponseEntityWrapper> call, Response<ResponseEntityWrapper> response) {
//                ResponseEntityWrapper body = response.body();
//                if (body != null) {
//                    log.info("code:{},message:{},data{}", body.getStatusCode(), body.getStatusMessage(), body.getData());
//                } else {
//                    log.error("body is null");
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseEntityWrapper> call, Throwable t) {
//                log.error("Throwable:{}", t);
//            }
//        });
    }
}
