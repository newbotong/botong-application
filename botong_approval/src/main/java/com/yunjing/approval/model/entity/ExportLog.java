package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 导出记录
 * @author 刘小鹏
 * @date 2018/01/15
 */
@Data
@TableName("approval_export_log")
@EqualsAndHashCode(callSuper = true)
public class ExportLog extends BaseModel<ExportLog> {

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 企业主键
     */
    @TableField("org_id")
    private Long org_id;

    /**
     * 用户主键
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 记录时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 审批类型主键
     */
    @TableField("model_id")
    private String  modelId;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
