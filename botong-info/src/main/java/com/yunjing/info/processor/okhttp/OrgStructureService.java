package com.yunjing.info.processor.okhttp;

import com.yunjing.info.dto.Member;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

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
    @GET("/api/microapps/appcenter/org/find-sub-lists")
    Call<ResponseEntityWrapper<List<Member>>> findSubLists(@Query("deptIds") String deptIds, @Query("memberIds") String memberIds, @Query("simplify") Integer simplify);
}
