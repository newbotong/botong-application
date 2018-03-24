package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
@TableName("approval_process")
@EqualsAndHashCode(callSuper = true)
public class ApprovalProcess extends BaseModel<ApprovalProcess> {

    /**
     * 审批主键
     */
    @TableField("approval_id")
    private String approvalId;

    /**
     * 审批人主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 状态 0:未处理 1:同意 2:拒绝 3:转交 4.撤回
     */
    @TableField("process_state")
    private Integer processState;

    /**
     * 排序
     */
    @TableField("seq")
    private Integer seq;

    /**
     * 模型ID
     */
    @TableField("process_time")
    private Timestamp processTime;

    /**
     * 标题
     */
    @TableField("reason")
    private String reason;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
