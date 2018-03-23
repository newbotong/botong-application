package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author roc
 * @date 2018/1/15
 */
@Data
@TableName("approval_process")
@EqualsAndHashCode(callSuper = true)
public class ApprovalProcess extends Model<ApprovalProcess> {

    /**
     * 流程主键
     */
    @TableId(value = "process_id", type = IdType.UUID)
    private String processId;

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
    @TableField("state")
    private Integer state;

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

    @Override
    protected Serializable pkVal() {
        return this.processId;
    }
}
