package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * 审批人视图
 *
 * @author 刘小鹏
 * @date 2018/04/08
 */
@Data
public class ApproverVO {

    /**
     * 成员id
     */
    private String memberId;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 审批人手机号
     */
    private String mobile;

    /**
     * 审批人名称
     */
    private String name;

    /**
     * 账号id
     */
    private String passpottld;

    /**
     * 头像
     */
    private String profile;
}
