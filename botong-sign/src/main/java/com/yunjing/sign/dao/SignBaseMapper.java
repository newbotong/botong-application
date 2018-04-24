package com.yunjing.sign.dao;

import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.sign.beans.model.SignDetail;
import com.yunjing.sign.beans.param.SignMapperParam;
import com.yunjing.sign.beans.vo.SignDetailVO;
import com.yunjing.sign.beans.vo.SignExcelVO;
import com.yunjing.sign.beans.vo.SignMonthVO;
import com.yunjing.sign.beans.vo.SignUserInfoVO;

import java.util.List;

/**
 * <p>
  * 签到明细 Mapper 接口
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
public interface SignBaseMapper<T> extends IBaseMapper<T> {

    /**
     * 根据ids查询状态
     * @param signMapperParam 签到参数
     * @return
     */
    List<SignUserInfoVO> getCountInfo(SignMapperParam signMapperParam);


    /**
     * 查询我的签到明细
     * @param signMapperParam   签到参数
     * @return
     */
    List<SignDetailVO> queryMonthInfo(SignMapperParam signMapperParam);

    /**
     * 查询我的
     * @param signMapperParam   参数对象
     * @return
     */
    List<SignMonthVO> staticsMonthInfo(SignMapperParam signMapperParam);

    /**
     * 查询导出明细
     *
     * @param signMapperParam
     * @return
     */
    List<SignExcelVO> querySignDetail(SignMapperParam signMapperParam);

}