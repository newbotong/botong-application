package com.yunjing.sign.processor.okhttp;

import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.vo.SignUserInfoVO;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * <p>
 * <p> 应用中心 api 接口
 * </p>
 *
 * @author jingwj
 * @since 2018/4/3.
 */
public interface UserApiService {

    /**
     * 获取所有的人员id
     *
     * @param deptIds
     * @param memberIds
     * @return
     */
    @GET("/api/microapps/appcenter/org/find-sub-lists")
    Call<ResponseEntityWrapper<List<SignUserInfoVO>>> findSubLists(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds, @Query("simplify") int simplify);

    /**
     * 获取企业成员管理范围
     *
     * @param appId
     * @param memberId
     * @return
     */
    @GET("/api/microapps/appcenter/org/admin/manage-scope")
    Call<ResponseEntityWrapper<List<SignUserInfoVO>>> manageScope(@Query("appId") String appId, @Query("memberId") String memberId);


    /**
     * 分页获取人员id
     * @param deptIds
     * @param memberIds
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GET("/api/microapps/appcenter/org/find-member-page")
    Call<ResponseEntityWrapper<PageWrapper<SignUserInfoVO>>> findMemberPage(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds,
                                                                            @Query("pageNo") int pageNo, @Query("pageSize")int pageSize);

}
