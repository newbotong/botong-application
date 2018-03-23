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
@TableName("approval_comment_img")
@EqualsAndHashCode(callSuper = true)
public class CommentImg extends Model<CommentImg> {

    /**
     * 图片主键
     */
    @TableId("img_id")
    private String imgId;

    /**
     * 评论主键
     */
    @TableField("comment_id")
    private String commentId;

    /**
     * 图片地址
     */
    @TableField("img_url")
    private String imgUrl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 是否使用 0:未使用 1:已使用
     */
    @TableField("is_used")
    private String isUsed;

    @Override
    protected Serializable pkVal() {
        return this.imgId;
    }
}
