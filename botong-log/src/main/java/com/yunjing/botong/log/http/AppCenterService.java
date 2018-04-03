package com.yunjing.botong.log.http;

import com.yunjing.botong.log.processor.feign.param.DangParam;
import com.yunjing.mommon.base.PushParam;

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

}
