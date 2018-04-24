package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/11/22
 */
@Data
public class SetConditionVO {

    /**
     * 条件主键
     */
    private String conditionId;

    /**
     * 模型主键
     */
    private String modelId;

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
    private Integer enabled;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 审批人信息
     */
    private List<UserVO> userVo;

}
