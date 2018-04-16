package com.yunjing.sign.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.sign.beans.model.SignConfigModel;
import com.yunjing.sign.beans.param.SignConfigParam;
import com.yunjing.sign.beans.vo.SignConfigVO;

/**
 * <p>
 * 签到配置 服务类
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
public interface ISignConfigService extends IService<SignConfigModel> {

    /**
     * 设置签到规则
     * @param signConfigParam 签到规则对象
     * @return                成功与否
     */
    boolean setSignConfig(SignConfigParam signConfigParam);

    /**
     * 查询签到规则
     * @param orgId
     * @return
     */
    SignConfigVO getSignConfig(String orgId);
	
}
