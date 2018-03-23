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
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
@TableName("approval")
@EqualsAndHashCode(callSuper = true)
public class Approval extends Model<Approval> {

    /**
     * 审批ID
     */
    @TableId(value = "approval_id", type = IdType.UUID)
    private String approvalId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 模型ID
     */
    @TableField("model_id")
    private String modelId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 完成时间
     */
    @TableField("finish_time")
    private Timestamp finishTime;

    /**
     * 状态 0:审批中 1:审批完成 2:已撤回
     */
    @TableField("state")
    private Integer state;

    /**
     * 结果 1:已同意 2:已拒绝 4:已撤销
     */
    @TableField("result")
    private Integer result;

    /**
     * 企业ID
     */
    @TableField("org_id")
    private String orgId;


    /**
     * 模型版本
     */
    @TableField("model_version")
    private Integer modelVersion;

    @Override
    protected Serializable pkVal() {
        return this.approvalId;
    }
}
