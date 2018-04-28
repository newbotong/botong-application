package com.yunjing.approval.transfer;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author: 曲天宇
 * @date: Created in 10:22 2018/3/26
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberVo {
    /**
     * 主键id
     */
    private String id;
    /**
     * 成员对应账户Id
     */
    private String passportId;
    /**
     * 成员所在企业Id
     */
    private String companyId;

}
