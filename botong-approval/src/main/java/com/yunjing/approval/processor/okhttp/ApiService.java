package com.yunjing.approval.processor.okhttp;

import com.yunjing.approval.model.vo.Member;
import com.yunjing.approval.model.vo.MemberInfo;
import com.yunjing.approval.model.vo.OrgMemberVo;
import com.yunjing.approval.param.DangParam;
import com.yunjing.approval.param.PushParam;
import com.yunjing.approval.param.SchedulerParam;
import com.yunjing.mommon.wrapper.PageWrapper;
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
     * 服务器地址，必须以 / 结尾，别问我为啥，因为没有 / 会报错
     */
    String BASE_URL = "http://192.168.10.233:8000/";

    /**
     * 推送
     *
     * @param param
     * @return
     */
    @POST("/api/microapps/appcenter/push/push-target-user")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseEntityWrapper> push(@Body PushParam param);

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
     * @param memberId 测试数据：【611564163546121654982
     *                 6384302108069335040】
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
    Call<ResponseEntityWrapper<List<OrgMemberVo>>> findAllOrgMember(@Query("orgId") String orgId);

    /**
     * 设置任务调度
     *
     * @param param
     * @return
     */
    @POST("/api/microapps/appcenter/scheduler/set")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseEntityWrapper<Long>> setTask(@Body SchedulerParam param);

    /**
     * 获取所有的人员id
     *
     * @param deptIds
     * @param memberIds
     * @return
     */
    @GET("/api/microapps/appcenter/org/find-sub-lists")
    Call<ResponseEntityWrapper<List<Member>>> findSubLists(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds, @Query("simplify") int simplify);

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
     * 分页获取人员id
     * @param deptIds
     * @param memberIds
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GET("/api/microapps/appcenter/org/find-member-page")
    Call<ResponseEntityWrapper<PageWrapper<Member>>> findMemberPage(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds,
                                                                            @Query("pageNo") int pageNo, @Query("pageSize")int pageSize);
}
