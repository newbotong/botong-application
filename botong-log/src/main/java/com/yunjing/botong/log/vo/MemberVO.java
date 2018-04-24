package com.yunjing.botong.log.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Set;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/3/2 15:08
 * @description:
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberVO implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 用户id
     */
    private String passportId;

    /**
     * 成员所在企业Id
     */
    private String companyId;
    /**
     * 职位
     */
    private String position;
    /**
     * 电话号
     */
    private String mobile;

    /**
     * 头像
     */
    private String profile;

}
