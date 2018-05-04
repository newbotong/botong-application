package com.yunjing.approval.processor.okhttp;

import com.yunjing.approval.model.vo.Member;
import com.yunjing.approval.model.vo.MemberInfo;
import com.yunjing.approval.model.vo.OrgMemberVo;
import com.yunjing.approval.param.DangParam;
import com.yunjing.approval.param.PushParam;
import com.yunjing.approval.param.SchedulerParam;
import com.yunjing.message.share.org.OrgMemberMessage;
import com.yunjing.mommon.wrapper.PageWrapper;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * <p> 应用中心api
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/3.
 */
public interface AppCenterService {


    /**
     * 推送
     *
     * @param param
     */
    void push(PushParam param);


    /**
     * dang
     *
     * @param param
     */
    void dang(DangParam param);


    /**
     * 校验用户权限
     *
     * @param appId
     * @param memberId
     * @param isSync   是否同步方式执行
     * @return
     */
    boolean isManager(String appId, String memberId, boolean isSync);

    /**
     * 获取指定企业所有成员信息
     *
     * @param orgId
     * @param isSync 是否同步方式执行
     * @return
     */
    List<OrgMemberVo> findAllOrgMember(String orgId, boolean isSync);

    /**
     * 设置任务调度
     *
     * @param param
     * @return
     */
    Long setTask(SchedulerParam param);

    /**
     * 验证管理员回调
     */
    interface VerifyManagerCallback {

        /**
         * 验证是否是管理员回调
         *
         * @param isManager
         */
        void verify(boolean isManager);
    }


    /**
     * 组织架构成员回调
     */
    interface OrgMemberCallback {
        /**
         * 回调成员信息
         *
         * @param infos
         */
        void result(List<OrgMemberVo> infos);
    }

    /**
     * 部门成员回调
     */
    interface MemberCallback {
        /**
         * 回调成员信息
         *
         * @param infos
         */
        void result(List<MemberInfo> infos);
    }

    /**
     * 任务调度回调
     */
    interface TaskCallback {

        /**
         * 回调任务调度结果
         *
         * @param taskId
         */
        void taskCallback(Long taskId);
    }

    /**
     * 获取所有的人员id
     *
     * @param deptIds
     * @param memberIds
     * @return
     */
    List<Member> findSubLists(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds);

    /**
     * 分页获取人员id
     *
     * @param deptIds
     * @param memberIds
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageWrapper<Member> findMemberPage(@Query("deptIds") String[] deptIds, @Query("memberIds") String[] memberIds,
                                       @Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    /**
     * 获取企业成员管理范围
     *
     * @param appId
     * @param memberId
     * @return
     */
    List<Member> manageScope(@Query("appId") String appId, @Query("memberId") String memberId);

    /**
     * 获取部门主管
     *
     * @param companyId
     * @param memberId
     * @return
     */
    Map<String, List<OrgMemberMessage>> findDeptManager(@Query("companyId") String companyId, @Query("memberId") String memberId);

}
