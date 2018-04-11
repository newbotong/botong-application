package com.yunjing.notice.processor.okhttp;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * okhttp调用组织机构
 *
 * @author tandk
 * @date 2018/4/4 11:47
 */
public interface OrgStructureService {

    /**
     * 获取部门下的成员列表
     *
     * @param deptIds   部门id集合,逗号隔开
     * @param memberIds 成员id集合,逗号隔开
     * @return Call<ResponseEntityWrapper>
     */

    @POST("/api/microapps/appcenter/org/find-sub-lists")
    Call<ResponseEntityWrapper> findSubLists(@Query("deptIds") String deptIds, @Query("memberIds") String memberIds, @Query("simplify") Integer simplify);
}
