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
@TableName("approval")
@EqualsAndHashCode(callSuper = true)
public class Approval extends BaseModel<Approval> {

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
     * 审批所属部门ID
     */
    @TableField("dept_id")
    private String deptId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 完成时间
     */
    @TableField("finish_time")
    private Long finishTime;

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

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

}
