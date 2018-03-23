package com.yunjing.sign.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yunjing.sign.beans.model.SignDetailDaily;
import com.yunjing.sign.beans.param.SignMapperParam;
import com.yunjing.sign.beans.vo.SignDetailVO;
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
public interface SignDetailDailyMapper extends BaseMapper<SignDetailDaily> {

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

}