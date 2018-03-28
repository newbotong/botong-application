package com.yunjing.approval.model.dto;

import com.yunjing.approval.model.entity.ApprovalAttr;
import lombok.Data;

/**
 * 审批详情内部的明细
 *
 * @author 刘小鹏
 * @date 2018/03/26
 */
@Data
public class InternalDetailDTO {

    /**
     * 明细的key
     */
    private String keyName;

    /**
     * 明细内的属性名称
     */
    private String attrName;

    /**
     * 明细内的属性值
     */
    private String attrValue;

    /**
     * 数据类型
     */
    private Integer dataType;

    /**
     * 时间区间的字段描述
     */
    private String optValue;
}
