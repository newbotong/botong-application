package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 企业模型
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
@TableName("org_model")
@EqualsAndHashCode(callSuper = true)
public class OrgModel extends BaseModel<OrgModel> {

    /**
     * 企业(组织)主键
     */
    @TableField("org_id")
    private Long orgId;

    /**
     * 模型主键
     */
    @TableField("model_id")
    private Long modelId;

    /**
     * 类型 1:日志 2:审批
     */
    @TableField("data_type")
    private Integer dataType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}