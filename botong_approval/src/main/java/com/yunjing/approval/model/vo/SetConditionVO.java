package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author lxp
 * @date 2017/11/22
 */
@Data
public class SetConditionVO {

    /**
     * 条件主键
     */
    private Long conditionId;

    /**
     * 模型主键
     */
    private Long modelId;

    /**
     * 条件描述
     */
    private String content;

    /**
     * 条件
     */
    private String cdn;

    /**
     * 0: 无效 1: 有效
     */
    private int enabled;

    /**
     * 顺序
     */
    private int sort;

    /**
     * 审批人信息
     */
    private List<UserVO> userVo;

}
