package com.yunjing.botong.log.processor.okhttp.impl;

import com.yunjing.botong.log.params.DangParam;
import com.yunjing.botong.log.params.SchedulerParam;
import com.yunjing.botong.log.processor.okhttp.ApiService;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.vo.MemberInfo;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
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

    /**
     * api 服务
     */
    private ApiService apiService;


    /**
     * 管理员验证回调接口
     */
    private VerifyManagerCallback verifyManagerCallback;

    /**
     * 组织架构成员回调接口
     */
    private OrgMemberCallback orgMemberCallback;

    /**
     * 任务调度回调
     */
    private TaskCallback taskCallback;

    /**
     * 设置管理员验证回调
     *
     * @param verifyManagerCallback
     */
    public void setVerifyManagerCallback(VerifyManagerCallback verifyManagerCallback) {
        this.verifyManagerCallback = verifyManagerCallback;
    }

    /**
     * 设置组织架构成员回调接口
     *
     * @param orgMemberCallback
     */
    public void setOrgMemberCallback(OrgMemberCallback orgMemberCallback) {
        this.orgMemberCallback = orgMemberCallback;
    }


    public void setTaskCallback(TaskCallback taskCallback) {
        this.taskCallback = taskCallback;
    }

    public AppCenterServiceImpl() {

        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(appCenterUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂【FastJsonConverterFactory.create()】
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
                        log.info("调用推送结果，code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
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
                log.error("Throwable:{}", t);
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
                    log.info("调用 dang 结果，code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
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

    @Override
    public boolean isManager(String appId, String memberId, boolean isSync) {

        Call<ResponseEntityWrapper<Boolean>> call = apiService.verifyManager(appId, memberId);
        if (isSync) {
            try {
                // 同步方式请求
                Response<ResponseEntityWrapper<Boolean>> response = call.execute();
                ResponseEntityWrapper<Boolean> body = response.body();
                if (body != null) {
                    log.info("获取是否是管理员结果，code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
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
        } else {
            call.enqueue(new Callback<ResponseEntityWrapper<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseEntityWrapper<Boolean>> call, Response<ResponseEntityWrapper<Boolean>> response) {
                    ResponseEntityWrapper<Boolean> body = response.body();
                    if (body != null && verifyManagerCallback != null) {
                        log.info("code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
                        verifyManagerCallback.verify(body.getData());
                    } else {
                        log.error("body is null");
                    }
                }

                @Override
                public void onFailure(Call<ResponseEntityWrapper<Boolean>> call, Throwable t) {

                }
            });
        }
        return false;
    }

    @Override
    public List<MemberInfo> findAllOrgMember(String orgId, boolean isSync) {
        Call<ResponseEntityWrapper<List<MemberInfo>>> call = apiService.findAllOrgMember(orgId);
        if (isSync) {
            try {
                Response<ResponseEntityWrapper<List<MemberInfo>>> response = call.execute();
                ResponseEntityWrapper<List<MemberInfo>> body = response.body();
                if (body != null) {
                    log.info("获取指定企业所有成员信息:code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
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
        } else {
            call.enqueue(new Callback<ResponseEntityWrapper<List<MemberInfo>>>() {
                @Override
                public void onResponse(Call<ResponseEntityWrapper<List<MemberInfo>>> call, Response<ResponseEntityWrapper<List<MemberInfo>>> response) {
                    ResponseEntityWrapper<List<MemberInfo>> body = response.body();
                    if (body != null && orgMemberCallback != null) {
                        log.info("code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
                        orgMemberCallback.result(body.getData());
                    } else {
                        log.error("body is null");
                    }
                }

                @Override
                public void onFailure(Call<ResponseEntityWrapper<List<MemberInfo>>> call, Throwable t) {
                    log.error("Throwable:{}", t);
                }
            });
        }
        return null;
    }

    @Override
    public Long setTask(SchedulerParam param) {
        try {
            Response<ResponseEntityWrapper<Long>> response = apiService.setTask(param).execute();
            ResponseEntityWrapper<Long> body = response.body();
            if (body != null) {
                log.info("设置任务调度结果:code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
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

    @Override
    public List<MemberInfo> manageScope(String appId, String memberId) {
        try {
            Response<ResponseEntityWrapper<List<MemberInfo>>> response = apiService.manageScope(appId, memberId).execute();
            ResponseEntityWrapper<List<MemberInfo>> body = response.body();
            if (body != null) {
                log.info("获取管理范围：code:{}，message:{}", body.getStatusCode(), body.getStatusMessage());
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
}
