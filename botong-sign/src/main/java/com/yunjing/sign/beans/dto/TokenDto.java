package com.yunjing.sign.beans.dto;

import com.yunjing.mommon.base.BaseDto;
import lombok.Data;

/**
 * @version 1.0.0
 * @author: Gyb
 * @date 11/03/2018
 * @description
 **/
@Data
public class TokenDto extends BaseDto {

    private String accessToken;
    private String refreshToken;
}
