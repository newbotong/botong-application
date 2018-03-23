package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author roc
 * @date 2017/12/21
 */
@Data
@TableName("approval_copy")
@EqualsAndHashCode(callSuper=true)
public class Copy extends Model<Copy> {

    /**
     * 抄送人主键
     */
    @TableId("copy_id")
    private String copyId;

    /**
     * 模型主键
     */
    @TableField("model_id")
    private String modelId;

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

    @Override
    protected Serializable pkVal() {
        return this.copyId;
    }
}
