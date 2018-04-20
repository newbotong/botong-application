package com.yunjing.sign.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.sign.beans.model.SignConfigDaily;
import com.yunjing.sign.beans.model.SignConfigModel;
import com.yunjing.sign.beans.param.SignConfigParam;
import com.yunjing.sign.beans.vo.SignConfigVO;
import com.yunjing.sign.constant.SignConstant;
import com.yunjing.sign.dao.mapper.SignConfigMapper;
import com.yunjing.sign.processor.okhttp.UserRemoteApiService;
import com.yunjing.sign.service.ISignConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 签到配置 服务实现类
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
@Service
@Slf4j
public class SignConfigServiceImpl extends ServiceImpl<SignConfigMapper, SignConfigModel> implements ISignConfigService {


    @Autowired
    private UserRemoteApiService userRemoteApiService;

    /**
     * 设置签到规则
     *
     * @param signConfigParam 签到规则对象
     * @return 成功与否
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setSignConfig(SignConfigParam signConfigParam) {
        SignConfigModel signConfig = BeanUtils.map(signConfigParam, SignConfigModel.class);
        SignConfigModel signConfigModel = new SignConfigModel().selectOne(new EntityWrapper<SignConfigModel>().eq("org_id", signConfigParam.getOrgId()));
        boolean result = false;
        //判断是否存在数据库中，存在就更新，不存在新增
        if (signConfigModel != null) {
            signConfig.setId(signConfigModel.getId());
            if (signConfig.getTimeStatus() == SignConstant.BOTONG_ZERO_VALUE.intValue()) {
                signConfig.setStartTime(SignConstant.EMPTY_STR);
                signConfig.setEndTime(SignConstant.EMPTY_STR);
            }
            result = signConfig.updateById();
        } else {
            signConfig.setId(IDUtils.uuid());
            result = signConfig.insert();
        }
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
        SignConfigModel signConfigModel = new SignConfigModel().selectOne(new EntityWrapper<SignConfigModel>().eq("org_id", orgId));
        SignConfigVO vo = null;
        if (signConfigModel != null) {
            vo = BeanUtils.map(signConfigModel, SignConfigVO.class);
        }
        return vo;
    }

    /**
     * 校验用户权限
     *
     * @param appId    应用id
     * @param memberId 成员Id
     * @return 成功与否
     */
    @Override
    public boolean verifyManager(String appId, String memberId) {
        if (StringUtils.isEmpty(memberId)) {
            throw new ParameterErrorException("成员id不能为空");
        }
        return userRemoteApiService.verifyManager(appId, memberId);
    }
}
