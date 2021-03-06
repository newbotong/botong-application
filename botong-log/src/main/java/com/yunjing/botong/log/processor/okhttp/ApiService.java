package com.yunjing.botong.log.processor.okhttp;

import com.yunjing.botong.log.params.DangParam;
import com.yunjing.botong.log.params.SchedulerParam;
import com.yunjing.botong.log.vo.AppPushParam;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * <p>
 * <p> 应用中心 api 接口
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/3.
 */
public interface ApiService {

    /**
     * 服务器地址，必须以 / 结尾
     */
    String BASE_URL = "http://192.168.10.229:8000/";

    /**
     * 推送
     *
     * @param param
     * @return
     */
    @POST("/api/microapps/appcenter/push/push-target-user")
    // 设置请求头，post body方式提交
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseEntityWrapper> push(@Body AppPushParam param);

    /**
     * dang
     *
     * @param param
     * @return
     */
    @POST("/api/microapps/appcenter/dang/send-dang")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseEntityWrapper> dang(@Body DangParam param);

    /**
     * 校验用户权限
     *
     * @param appId
     * @param memberId
     * @return
     */
    @GET("/api/microapps/appcenter/org/verify-manager")
    Call<ResponseEntityWrapper<Boolean>> verifyManager(@Query("appId") String appId, @Query("memberId") String memberId);


    /**
     * 获取指定企业所有成员信息
     *
     * @param orgId
     * @return
     */
    @GET("/api/microapps/appcenter/user/find-all-org-member")
    Call<ResponseEntityWrapper<List<Member>>> findAllOrgMember(@Query("orgId") String orgId);


    /**
     * 获取企业成员管理范围
     *
     * @param appId
     * @param memberId
     * @return
     */
    @GET("/api/microapps/appcenter/org/admin/manage-scope")
    Call<ResponseEntityWrapper<List<Member>>> manageScope(@Query("appId") String appId, @Query("memberId") String memberId);

    /**
     * 设置任务调度
     *
     * @param param
     * @return
     */
    @POST("/api/microapps/appcenter/scheduler/set")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseEntityWrapper<String>> setTask(@Body SchedulerParam param);


    /**
     * 获取所有的人员id
     * @param deptIds
     * @param memberIds
     * @param simplify
     * @return
     */
    @GET("/api/microapps/appcenter/org/find-sub-lists")
    Call<ResponseEntityWrapper<List<Member>>> findSubLists(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds, @Query("simplify") int simplify);

}
