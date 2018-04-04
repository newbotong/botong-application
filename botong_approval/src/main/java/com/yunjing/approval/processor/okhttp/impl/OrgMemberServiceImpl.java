package com.yunjing.approval.processor.okhttp.impl;

import com.yunjing.approval.processor.okhttp.OrgMemberService;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

import java.io.IOException;

/**
 * @author 谈东魁
 * @date 2018/4/3 15:58
 */
@Slf4j
@Service
public class OrgMemberServiceImpl implements OrgMemberService {

    @Value("${okhttp.botong-org-structure}")
    String BASE_URL;

    private OrgMemberService service;

    private void initRetrofit() {
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 构建请求对象
        service = retrofit.create(OrgMemberService.class);
    }


    @Override
    @POST
    public Call<ResponseEntityWrapper> getMemberList(String companyId, String[] passportIds) {
        if(service == null){
            initRetrofit();
        }
        try {
            Call<ResponseEntityWrapper> call = service.getMemberList(companyId, passportIds);
            call.execute();
            return call.clone();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
