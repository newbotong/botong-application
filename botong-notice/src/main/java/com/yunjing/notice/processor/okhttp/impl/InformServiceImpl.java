package com.yunjing.notice.processor.okhttp.impl;

import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.okhttp.InformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * @author 谈东魁
 * @date 2018/4/3 15:58
 */
@Slf4j
@Service
public class InformServiceImpl implements InformService {

    @Value("${okhttp.botong-third-party}")
    String baseUrl;

    private InformService service;
    private void initRetrofit() {
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 构建请求对象
        service = retrofit.create(InformService.class);
    }

    /**
     * 发送推送消息
     *
     * @param pushParam 推送消息
     * @return ResponseEntityWrapper
     */
    @Override
    public Call<ResponseEntityWrapper> pushAllTargetByUser(PushParam pushParam) {
        if(service == null){
            initRetrofit();
        }
        try {
            Call<ResponseEntityWrapper> call = service.pushAllTargetByUser(pushParam);
            call.execute();
            return call.clone();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
