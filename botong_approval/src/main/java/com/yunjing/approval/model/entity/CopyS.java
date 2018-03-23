package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 *
 * @author 刘小鹏
 * @date 2018/01/15
 */
@Data
@TableName("approval_copys")
@EqualsAndHashCode(callSuper = true)
public class CopyS extends Model<CopyS> {

    /**
     * 抄送主键
     */
    @TableId("copys_id")
    private String copySId;

    /**
     * 用户主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 审批主键
     */
    @TableField("approval_id")
    private String approvalId;

    /**
     * 类型
     */
    @TableField("copys_type")
    private String copySType;

    /**
     * 抄送时间
     */
    @TableField("copys_time")
    private Timestamp copySTime;

    @Override
    protected Serializable pkVal() {
        return this.copySId;
    }
}
