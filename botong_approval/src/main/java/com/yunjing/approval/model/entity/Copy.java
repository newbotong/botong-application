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
@TableName("approval_copy")
@EqualsAndHashCode(callSuper=true)
public class Copy extends BaseModel<Copy> {

    /**
     * 模型主键
     */
    @TableField("model_id")
    private Long modelId;

    /**
     * 用户主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 区分人员与主管
     */
    @TableField("type")
    private int type;

    /**
     * 顺序
     */
    @TableField("sort")
    private int sort;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

}
