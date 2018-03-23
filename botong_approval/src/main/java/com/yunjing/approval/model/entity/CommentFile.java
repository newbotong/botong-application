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
@TableName("approval_comment_file")
@EqualsAndHashCode(callSuper = true)
public class CommentFile extends Model<CommentFile> {

    /**
     * 文件主键
     */
    @TableId("file_id")
    private String fileId;

    /**
     * 评论主键
     */
    @TableField("comment_id")
    private String commentId;

    /**
     * 文件地址
     */
    @TableField("file_url")
    private String fileUrl;

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
        return this.fileId;
    }
}
