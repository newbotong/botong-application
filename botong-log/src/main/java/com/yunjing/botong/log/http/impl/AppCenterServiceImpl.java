package com.yunjing.botong.log.http.impl;

import com.yunjing.botong.log.http.ApiService;
import com.yunjing.botong.log.http.AppCenterService;
import com.yunjing.botong.log.processor.feign.param.DangParam;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <p>
 * <p> 应用中心api统一处理服务
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/3.
 */
@Slf4j
@Service
public class AppCenterServiceImpl implements AppCenterService {

    /**
     * 这里需要在启动参数时指定，无法放在配置中心加载
     */
    @Value("app-center-url")
    private String appCenterUrl = ApiService.BASE_URL;

    private ApiService apiService;

    public AppCenterServiceImpl() {

        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(appCenterUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 构建请求对象
        apiService = retrofit.create(ApiService.class);
    }

    @Override
    public void push(PushParam param) {
        apiService.push(param).enqueue(new Callback<ResponseEntityWrapper>() {

            /**
             * 成功回调，服务器正常响应，200、400、404、500都会进这里
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<ResponseEntityWrapper> call, Response<ResponseEntityWrapper> response) {
                // 服务器响应数据
                ResponseEntityWrapper body = response.body();
                // 服务器响应code
                int code = response.code();

                if (response.isSuccessful()) {
                    if (body != null) {
                        log.info("code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
                    } else {
                        log.error("body is null");
                    }
                } else {
                    // 这里处理非响应 200 的情况
                }
            }

            /**
             * 服务器连接失败会到这里
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<ResponseEntityWrapper> call, Throwable t) {

            }
        });
    }

    @Override
    public void dang(DangParam param) {
        apiService.dang(param).enqueue(new Callback<ResponseEntityWrapper>() {
            @Override
            public void onResponse(Call<ResponseEntityWrapper> call, Response<ResponseEntityWrapper> response) {
                ResponseEntityWrapper body = response.body();
                if (body != null) {
                    log.info("code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
                } else {
                    log.error("body is null");
                }
            }

            @Override
            public void onFailure(Call<ResponseEntityWrapper> call, Throwable t) {
                log.error("Throwable:{}", t);
            }
        });
    }
}
