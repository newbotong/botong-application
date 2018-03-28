package com.yunjing.sign.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.sign.beans.model.SignDetail;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.*;
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
public interface ISignDetailService extends IService<SignDetail> {

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
    SignListVO getCountInfo(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper);

    /**
     * 按月查询我签到的明细
     *
     * @param signDetailParam 签到明细
     * @return
     */
    MySignVO queryMonthInfo(SignDetailParam signDetailParam, SignBaseMapper mapper);


    /**
     * 按月统计指定部门和人员的考勤信息
     *
     * @param userAndDeptParam  部门和人员
     * @return
     */
    PageWrapper<UserMonthListVO> staticsMonthInfo(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper);

    /**
     * 获取导出模板
     * @param userAndDeptParam
     * @return
     */
    BaseExModel createTempExcel(UserAndDeptParam userAndDeptParam);

    /**
     * 查询所有的导出数据
     * @param userAndDeptParam
     * @param mapper
     * @return
     */
    List<SignExcelVO> getSignInList(UserAndDeptParam userAndDeptParam, SignBaseMapper mapper);
}
