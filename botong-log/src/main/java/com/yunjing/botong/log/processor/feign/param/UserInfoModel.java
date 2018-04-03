package com.yunjing.botong.log.processor.feign.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 刘舒杰
 * @date 2018/3/22 16:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoModel {

    private Long userId;

    private Long userTelephone;
}
