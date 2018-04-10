package com.yunjing.notice.processor.okhttp.impl;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.processor.okhttp.AuthorityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * okhttp调用权限验证
 *
 * @author 谈东魁
 * @date 2018/4/3 15:58
 */
@Slf4j
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Value("${okhttp.botong-org-admin}")
    String baseUrl;

    private AuthorityService service;

    private void initRetrofit() {
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 构建请求对象
        service = retrofit.create(AuthorityService.class);
    }

    /**
     * 是否有权限发送公告
     *
     * @param appId    应用编号
     * @param memberId 企业成员编号
     * @return ResponseEntityWrapper
     */
    @Override
    public Call<ResponseEntityWrapper> authority(String appId, String memberId) {
        if(service == null){
            initRetrofit();
        }
        try {
            Call<ResponseEntityWrapper> call = service.authority(appId, memberId);
            call.execute();
            return call.clone();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
