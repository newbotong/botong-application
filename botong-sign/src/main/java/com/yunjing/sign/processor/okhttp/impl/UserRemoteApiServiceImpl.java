package com.yunjing.sign.processor.okhttp.impl;

import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.vo.SignUserInfoVO;
import com.yunjing.sign.processor.okhttp.UserRemoteApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

/**
 * okhttp调用member服务
 * @author jingwj
 * @date 2018/4/3 15:58
 */
@Slf4j
@Service
public class UserRemoteApiServiceImpl implements UserRemoteApiService {

    @Value("${okhttp.botong-org-structure}")
    String baseUrl;

    private UserRemoteApiService userRemoteApiService;

    private void initRetrofit(){
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 构建请求对象
        userRemoteApiService = retrofit.create(UserRemoteApiService.class);
    }

    /**
     * 获取所有的人员id
     *
     * @param deptIds
     * @param memberIds
     * @return
     */
    @Override
    public Call<ResponseEntityWrapper<List<SignUserInfoVO>>> findSubLists(String[] deptIds, String[] memberIds) {
        if(userRemoteApiService == null){
            initRetrofit();
        }
        try {
            Call<ResponseEntityWrapper<List<SignUserInfoVO>>> call = userRemoteApiService.findSubLists(deptIds, memberIds);
            call.execute();
            return call.clone();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 分页获取人员id
     *
     * @param deptIds
     * @param memberIds
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Call<ResponseEntityWrapper<PageWrapper<SignUserInfoVO>>> findMemberPage(String[] deptIds, String[] memberIds, int pageNo, int pageSize) {
        if(userRemoteApiService == null){
            initRetrofit();
        }
        try {
            Call<ResponseEntityWrapper<PageWrapper<SignUserInfoVO>>> call = userRemoteApiService.findMemberPage(deptIds, memberIds, pageNo, pageSize);
            call.execute();
            return call.clone();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
