package com.yunjing.approval.model.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.sql.Timestamp;


/**
 * @author 刘小鹏
 * @date 2018/01/15
 */
@Data
public class CopySDTO {

    /**
     * 抄送主键
     */
    private String copySId;

    /**
     * 用户主键
     */
    private String userId;

    /**
     * 审批主键
     */
    private String approvalId;

    /**
     * 类型
     */
    private Integer copySType;

    /**
     * 抄送时间
     */
    private Timestamp copySTime;

}
