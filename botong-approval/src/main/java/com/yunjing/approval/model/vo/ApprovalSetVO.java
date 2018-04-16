package com.yunjing.approval.model.vo;

import com.yunjing.approval.model.entity.ModelL;
import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */

@Data
public class ApprovalSetVO {

    /** 不分条件 */
    private List<UserVO> users;

    /**
     * 分条件
     */
    private List<SetConditionVO> list;

    /**
     * 0:不分条件设置审批人 1:分条件设置审批人 2: 不设置
     */
    private Integer setting;

    /**
     * 审批模型
     */
    private ModelL modelL;
}
