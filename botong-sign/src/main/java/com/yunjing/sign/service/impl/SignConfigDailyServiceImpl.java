package com.yunjing.sign.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.sign.beans.model.SignConfigDaily;
import com.yunjing.sign.beans.model.SignConfigModel;
import com.yunjing.sign.beans.param.SignConfigParam;
import com.yunjing.sign.beans.vo.SignConfigVO;
import com.yunjing.sign.dao.mapper.SignConfigDailyMapper;
import com.yunjing.sign.service.ISignConfigDailyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  日常签到服务实现类
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@Service
public class SignConfigDailyServiceImpl extends ServiceImpl<SignConfigDailyMapper, SignConfigDaily> implements ISignConfigDailyService {

    /**
     * 设置签到规则
     *
     * @param signConfigParam 签到规则对象
     * @return 成功与否
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setSignConfig(SignConfigParam signConfigParam) {
        SignConfigDaily signConfig = BeanUtils.map(signConfigParam, SignConfigDaily.class);
        signConfig.setId(IDUtils.getID());
        boolean result = signConfig.insert();
        return result;
    }

    /**
     * 查询签到规则
     *
     * @param orgId
     * @return
     */
    @Override
    public SignConfigVO getSignConfig(String orgId) {
        SignConfigDaily signConfigModel = new SignConfigDaily().selectOne(new EntityWrapper<SignConfigDaily>().eq("org_id", orgId));
        SignConfigVO vo = null;
        if (signConfigModel != null) {
            vo = BeanUtils.map(signConfigModel, SignConfigVO.class);
        }
        return vo;
    }
}
