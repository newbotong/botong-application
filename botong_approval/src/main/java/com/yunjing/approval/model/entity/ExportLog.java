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
 * 导出记录
 * @author 刘小鹏
 * @date 2018/01/15
 */
@Data
@TableName("approval_export_log")
@EqualsAndHashCode(callSuper = true)
public class ExportLog extends Model {

    /**
     * 记录主键
     */
    @TableId(value = "log_id",type = IdType.UUID)
    private String logId;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 企业主键
     */
    @TableField("org_id")
    private String org_id;

    /**
     * 用户主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 记录时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 审批类型主键
     */
    @TableField("model_id")
    private String modelId;

    @Override
    protected Serializable pkVal() {
        return this.logId;
    }
}
