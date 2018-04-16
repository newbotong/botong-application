package com.yunjing.notice.processor.feign.param;

import lombok.Data;

/**
 * @author 刘舒杰
 * @date 2018/3/22 16:32
 */
@Data
public class UserInfoModel {
    /**
     * 接收人用户id
     */
    private String userId;
    /**
     * 接收用户手机号
     */
    private Long userTelephone;


}
