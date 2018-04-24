package com.yunjing.approval.model.dto;

import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
@Data
public class CopyDTO {

    /**
     * 抄送人主键
     */
    private String copyId;

    /**
     * 模型主键
     */
    private String modelId;

    /**
     * 用户主键
     */
    private String userId;

    /**
     * 区分人员与主管
     */
    private int type;

    /**
     * 顺序
     */
    private int sort;

}
