package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
@TableName("approval_sets")
@EqualsAndHashCode(callSuper = true)
public class ApprovalSets extends BaseModel<ApprovalSets> {

    /**
     * 模型主键
     */
    @TableField("model_id")
    private String modelId;
    /**
     * 0:不分条件设置审批人 1:分条件设置审批人
     */
    @TableField("setting")
    private Integer setting;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

}
