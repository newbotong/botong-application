package com.yunjing.sign.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.sign.beans.model.SignDetailDaily;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.MySignVO;
import com.yunjing.sign.beans.vo.SignDetailVO;
import com.yunjing.sign.beans.vo.SignListVO;
import com.yunjing.sign.beans.vo.UserMonthListVO;
import com.yunjing.sign.dao.SignBaseMapper;
import com.yunjing.sign.excel.BaseExModel;

import java.util.List;

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
    SignDetailVO toSign(SignDetailParam signDetailParam);

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

    /**
     * 获取导出模板
     * @param userAndDeptParam
     * @return
     */
    BaseExModel createTempExcel(UserAndDeptParam userAndDeptParam);

    /**
     * 按月统计指定部门和人员的考勤信息
     *
     * @param userAndDeptParam  部门和人员
     * @return
     */
    PageWrapper<UserMonthListVO> staticsMonthInfo(UserAndDeptParam userAndDeptParam);
    /**
     * 获取所有的签到明细
     * @param signDetailParam
     * @return
     */
    List<SignDetailDaily> queryDetailList(SignDetailParam signDetailParam);

}
