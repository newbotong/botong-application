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
@TableName("approval_copys")
@EqualsAndHashCode(callSuper = true)
public class Copys extends BaseModel<Copys> {

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
    private Integer copySType;

    /**
     * 类型
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
