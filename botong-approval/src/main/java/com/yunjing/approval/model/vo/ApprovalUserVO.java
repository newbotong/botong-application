package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
public class ApprovalUserVO {

    /**
     * 审批人主键
     */
    private String userId;
    /**
     * 审批人姓名
     */
    private String name;

    /**
     * 头像的背景颜色
     */
    private String color;

    /**
     * 审批人头像
     */
    private String avatar;

    /**
     * 头像中的名字
     */
    private String avatarName;

    /**
     * 审批状态
     */
    private Integer state;

    /**
     * 审批结果
     */
    private Integer result;

    /**
     * 审批时间
     */
    private Long approvalTime;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 备注信息
     */
    private String message;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 流程状态
     */
    private Integer processState;

    /**
     * 内容
     */
    private String content;

    /**
     * dang提醒
     */
    private Integer dangType;
    /**
     * 撤销状态
     */
    private Integer revokeState;
}
