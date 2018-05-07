package com.yunjing.approval.model.vo;


import lombok.Data;

import java.util.List;

/**
 * 审批条件和审批人视图
 *
 * @author 刘小鹏
 * @date 2018/05/07
 */
@Data
public class ConditionAndApproverVO {

    private List<ModelItemVO> modelItemList;

    private List<UserVO> approverList;
}
