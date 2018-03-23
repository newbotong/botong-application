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
 * 审批--评论
 * @author 刘小鹏
 * @date 2018/01/15
 */
@Data
@TableName("approval_comment")
@EqualsAndHashCode(callSuper = true)
public class Comment extends Model<Comment> {

    /**
     * 评论主键
     */
    @TableId("comment_id")
    private String commentId;

    /**
     * 审批主键
     */
    @TableField("approval_id")
    private String approvalId;

    /**
     * 用户主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 评论时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    @Override
    protected Serializable pkVal() {
        return this.commentId;
    }
}
