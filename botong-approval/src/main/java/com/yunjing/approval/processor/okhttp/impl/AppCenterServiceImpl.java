package com.yunjing.approval.processor.okhttp.impl;

import com.alibaba.fastjson.JSON;
import com.yunjing.approval.model.vo.Member;
import com.yunjing.approval.model.vo.MemberInfo;
import com.yunjing.approval.model.vo.OrgMemberVo;
import com.yunjing.approval.param.DangParam;
import com.yunjing.approval.param.PushParam;
import com.yunjing.approval.param.SchedulerParam;
import com.yunjing.approval.processor.okhttp.ApiService;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.PageWrapper;
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
    @Value("${okhttp.botong.zuul}")
    private String appCenterUrl;

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
     * 部门成员回调接口
     */
    private MemberCallback memberCallback;

    /**
     * 任务调度回调
     */
    private TaskCallback taskCallback;

    private void init() {
        log.info("appCenterUrl:{}", appCenterUrl);
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(appCenterUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂【FastJsonConverterFactory.create()】
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 构建请求对象
        apiService = retrofit.create(ApiService.class);
    }
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

    @Override
    public void push(PushParam param) {
        if (apiService == null) {
            init();
        }

        log.info("应用中心url:{}", appCenterUrl);

        log.info("推送参数：{}", JSON.toJSONString(param));

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
                if (body != null) {
                    log.info("调用推送结果，code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
                } else {
                    log.error("body is null");
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
        if (apiService == null) {
            init();
        }
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
        if (apiService == null) {
            init();
        }
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
    public List<OrgMemberVo> findAllOrgMember(String orgId, boolean isSync) {
        if (apiService == null) {
            init();
        }
        Call<ResponseEntityWrapper<List<OrgMemberVo>>> call = apiService.findAllOrgMember(orgId);
        if (isSync) {
            try {
                Response<ResponseEntityWrapper<List<OrgMemberVo>>> response = call.execute();
                ResponseEntityWrapper<List<OrgMemberVo>> body = response.body();
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
            call.enqueue(new Callback<ResponseEntityWrapper<List<OrgMemberVo>>>() {
                @Override
                public void onResponse(Call<ResponseEntityWrapper<List<OrgMemberVo>>> call, Response<ResponseEntityWrapper<List<OrgMemberVo>>> response) {
                    ResponseEntityWrapper<List<OrgMemberVo>> body = response.body();
                    if (body != null && orgMemberCallback != null) {
                        log.info("code:{},message:{}", body.getStatusCode(), body.getStatusMessage());
                        orgMemberCallback.result(body.getData());
                    } else {
                        log.error("body is null");
                    }
                }

                @Override
                public void onFailure(Call<ResponseEntityWrapper<List<OrgMemberVo>>> call, Throwable t) {
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

    /**
     * 获取所有的人员id
     *
     * @param deptIds
     * @param memberIds
     * @return
     */
    @Override
    public List<Member> findSubLists(String[] deptIds, String[] memberIds) {
        if (apiService == null) {
            init();
        }
        try {
            Response<ResponseEntityWrapper<List<Member>>> response = apiService.findSubLists(deptIds, memberIds, ApproConstants.BOTONG_ONE_NUM).execute();
            ResponseEntityWrapper<List<Member>> body = response.body();
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
    public PageWrapper<Member> findMemberPage(String[] deptIds, String[] memberIds, int pageNo, int pageSize) {
        if (apiService == null) {
            init();
        }
        try {
            Response<ResponseEntityWrapper<PageWrapper<Member>>> response = apiService.findMemberPage(deptIds, memberIds, pageNo, pageSize).execute();
            ResponseEntityWrapper<PageWrapper<Member>> body = response.body();
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
    public List<Member> manageScope(String appId, String memberId) {
        if (apiService == null) {
            init();
        }
        try {
            Response<ResponseEntityWrapper<List<Member>>> response = apiService.manageScope(appId, memberId).execute();
            ResponseEntityWrapper<List<Member>> body = response.body();
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


}
