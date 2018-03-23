package com.yunjing.sign.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.sign.beans.model.SignDetailDaily;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.MySignVO;
import com.yunjing.sign.beans.vo.SignListVO;

/**
 * <p>
 * 签到明细 服务类
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
public interface ISignDetailDailyService extends IService<SignDetailDaily> {
    /**
     * 签到
     * @param signDetailParam 签到对象
     * @return                成功与否
     */
    boolean toSign(SignDetailParam signDetailParam);

    /**
     * 查询当日签到次数
     * @param userId    用户id
     * @param orgId     组织id
     * @return          次数
     */
    int getSignCount(String userId, String orgId);

    /**
     * 签到统计接口
     *
     * @param userAndDeptParam 部门id和用户id组合
     * @return
     */
    SignListVO getCountInfo(UserAndDeptParam userAndDeptParam);

    /**
     * 按月查询我签到的明细
     *
     * @param signDetailParam 签到明细
     * @return
     */
    MySignVO queryMonthInfo(SignDetailParam signDetailParam);
}