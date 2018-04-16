package com.yunjing.sign.processor.okhttp.impl;

import com.alibaba.fastjson.JSON;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.vo.SignUserInfoVO;
import com.yunjing.sign.constant.SignConstant;
import com.yunjing.sign.processor.okhttp.UserApiService;
import com.yunjing.sign.processor.okhttp.UserRemoteApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    @Value("${okhttp.zuul}")
    String baseUrl;

    private UserApiService userRemoteApiService;

    private void initRetrofit(){
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 构建请求对象
        userRemoteApiService = retrofit.create(UserApiService.class);
    }

    /**
     * 获取所有的人员id
     *
     * @param deptIds
     * @param memberIds
     * @return
     */
    @Override
    public List<SignUserInfoVO> findSubLists(String[] deptIds, String[] memberIds) {
        if(userRemoteApiService == null){
            initRetrofit();
        }

        try {
            Response<ResponseEntityWrapper<List<SignUserInfoVO>>> response = userRemoteApiService.findSubLists(deptIds, memberIds, SignConstant.BOTONG_ZERO_VALUE).execute();
            ResponseEntityWrapper<List<SignUserInfoVO>> body = response.body();
            if (body != null) {
                log.info("根据部门id和用户id查询成员信息：code:{}，message:{}，data:{}", body.getStatusCode(), body.getStatusMessage(), JSON.toJSON(body.getData()));
                if (response.isSuccessful() && body.getStatusCode() == StatusCode.SUCCESS.getStatusCode()) {
                    return body.getData();
                }
            } else {
                log.error("body is null");
            }
            return null;
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
    public PageWrapper<SignUserInfoVO> findMemberPage(String[] deptIds, String[] memberIds, int pageNo, int pageSize) {
        if(userRemoteApiService == null){
            initRetrofit();
        }
        try {
            Response<ResponseEntityWrapper<PageWrapper<SignUserInfoVO>>> response = userRemoteApiService.findMemberPage(deptIds, memberIds, pageNo, pageSize).execute();
            ResponseEntityWrapper<PageWrapper<SignUserInfoVO>> body = response.body();
            if (body != null) {
                log.info("分页查询用户：code:{}，message:{}，data:{}", body.getStatusCode(), body.getStatusMessage(), JSON.toJSON(body.getData()));
                if (response.isSuccessful() && body.getStatusCode() == StatusCode.SUCCESS.getStatusCode()) {
                    return body.getData();
                }
            } else {
                log.error("body is null");
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取企业成员管理范围
     *
     * @param appId
     * @param memberId
     * @return
     */
    @Override
    public List<SignUserInfoVO> manageScope(String appId, String memberId) {
        if(userRemoteApiService == null){
            initRetrofit();
        }
        try {
            Response<ResponseEntityWrapper<List<SignUserInfoVO>>> response = userRemoteApiService.manageScope(appId, memberId).execute();
            ResponseEntityWrapper<List<SignUserInfoVO>> body = response.body();
            if (body != null) {
                log.info("获取管理范围：code:{}，message:{}，data:{}", body.getStatusCode(), body.getStatusMessage(), JSON.toJSON(body.getData()));
                if (response.isSuccessful() && body.getStatusCode() == StatusCode.SUCCESS.getStatusCode()) {
                    return body.getData();
                }
            } else {
                log.error("body is null");
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验用户权限
     *
     * @param appId    appId
     * @param memberId 成员Id
     * @return
     */
    @Override
    public boolean verifyManager(String appId, String memberId) {
        if(userRemoteApiService == null){
            initRetrofit();
        }
        Call<ResponseEntityWrapper<Boolean>> call = userRemoteApiService.verifyManager(appId, memberId);
        try {
            // 同步方式请求
            Response<ResponseEntityWrapper<Boolean>> response = call.execute();
            ResponseEntityWrapper<Boolean> body = response.body();
            if (body != null) {
                log.info("获取是否是管理员结果，code:{},message:{},data:{}", body.getStatusCode(), body.getStatusMessage(), JSON.toJSON(body.getData()));
                if (response.isSuccessful() && body.getStatusCode() == StatusCode.SUCCESS.getStatusCode()) {
                    return body.getData();
                }
            } else {
                log.error("body is null");
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
