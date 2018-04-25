package com.yunjing.info.processor.okhttp.impl;

import com.yunjing.info.processor.okhttp.CollectService;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * 收藏OKHttp
 *
 * @author 李双喜
 * @date 2018/4/8 16:17
 */
@Slf4j
@Service
public class CollectServiceImpl implements CollectService {

    @Value("${okhttp.botong.zuul}")
    String baseUrl;

    private CollectService service;

    private void initRetrofit() {
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 构建请求对象
        service = retrofit.create(CollectService.class);
    }


    /**
     * okHttp查询是否收藏
     *
     * @param userId   用户id
     * @param originId 资讯内容id
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @Override
    public Call<ResponseEntityWrapper> collectState(String userId, String originId) throws BaseException, IOException {
        Call<ResponseEntityWrapper> call;

        if (service == null) {
            initRetrofit();
        }
        call = service.collectState(userId,originId);
        return call;
    }
}
