package com.yunjing.sign.beans.param;

import lombok.Data;

/**
 * @version 1.0.0
 * @author: jingwj
 * @date 2018/3/13 15:41
 * @description
 **/
@Data
public class UserAndDeptParam {

    /**
     * 用户id 逗号间隔
     */
    private String userIds;

    private Integer pageNo;
    private Integer pageSize;

    /**
     * 成员Id
     */
    private String memberId;

    /**
     * 企业Id
     */
    private String orgId;

    /**
     * 部门id 逗号间隔
     */
    private String deptIds;

    private String signDate;


}
