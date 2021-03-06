package com.yunjing.botong.log.processor.okhttp;

import com.yunjing.botong.log.params.DangParam;
import com.yunjing.botong.log.params.SchedulerParam;
import com.yunjing.botong.log.vo.AppPushParam;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.mommon.base.PushParam;

import java.util.List;

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
    void push(AppPushParam param);


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
     * @return
     */
    boolean isManager(String appId, String memberId);

    /**
     * 获取指定企业所有成员信息
     *
     * @param orgId
     * @param isSync 是否同步方式执行
     * @return
     */
    List<Member> findAllOrgMember(String orgId, boolean isSync);


    /**
     * 设置任务调度
     *
     * @param param
     * @return
     */
    String setTask(SchedulerParam param);

    /**
     * 获取企业成员管理范围
     *
     * @param appId
     * @param memberId
     * @return
     */
    List<Member> manageScope(String appId, String memberId);

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
        void result(List<Member> infos);
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
     * 根据部门ids和成员ids查询所有的成员信息
     *
     * @param deptIds
     * @param memberIds
     * @return
     */
    List<Member> findSubLists(String[] deptIds, String[] memberIds);

}
