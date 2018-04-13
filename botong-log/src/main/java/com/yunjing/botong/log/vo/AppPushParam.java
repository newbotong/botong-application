package com.yunjing.botong.log.vo;

import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/4/12 11:19
 * @description
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class AppPushParam extends PushParam {

    /**
     * 企业编号
     */
    @NotNullOrEmpty
    private String companyId;

    /**
     * 应用编号
     */
    @NotNullOrEmpty
    private String appId;

}
