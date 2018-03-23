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
@TableName("approval_file")
@EqualsAndHashCode(callSuper = true)
public class ApprovalFile extends Model<ApprovalFile> {

    /**
     * 文件主键
     */
    @TableId("file_id")
    private String fileId;

    /**
     * 评论主键
     */
    @TableField("approval_id")
    private String approvalId;

    /**
     * 附件地址
     */
    @TableField("url")
    private String url;

    /**
     * 附件大小
     */
    @TableField("size")
    private String size;

    /**
     * 附件名称
     */
    @TableField("name")
    private String fileName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 顺序
     */
    @TableField("sort")
    private Integer sort;

    @Override
    protected Serializable pkVal() {
        return this.fileId;
    }
}
