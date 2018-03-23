package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
@Data
@TableName("model_category")
@EqualsAndHashCode(callSuper = true)
public class ModelCategory extends Model<ModelCategory>{

    /**
     * 分组主键
     */
    @TableId("category_id")
    private Long categoryId;

    /**
     * 企业主键
     */
    @TableField("org_id")
    private String orgId;

    /**
     * 分组名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 顺序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 是否删除
     */
    @TableField("is_delete")
    private Integer isDelete;

    @Override
    protected Serializable pkVal() {
        return this.categoryId;
    }
}
