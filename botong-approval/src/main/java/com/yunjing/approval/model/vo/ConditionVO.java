package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2018/05/07
 */
@Data
public class ConditionVO {

    private String field;

    private String value;

    private Integer type;

    private Integer judge;

    private String modelItemId;
}
