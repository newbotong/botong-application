package com.yunjing.sign.beans.param;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

/**
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class SignMapperParam {

    private String userIds;

    private String userId;

    private Long startDate;

    private Long endDate;
}
