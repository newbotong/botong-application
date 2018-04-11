package com.yunjing.notice.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author 李双喜
 * @date 2018/4/11 9:44
 */
@Data
@TableName("notice_log")
public class ExportNoticeLogEntity{
    /**
     * CREATE TABLE `notice_log` (
     *   `log_id` char(32) NOT NULL COMMENT '记录主键',
     *   `notice_id` char(32) NOT NULL COMMENT '主键',
     *   `user_id` char(32) NOT NULL COMMENT '接收用户主键',
     *   `is_read` int(11) NOT NULL DEFAULT '0' COMMENT '0:未读 1:已读',
     *   `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
     *   PRIMARY KEY (`log_id`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */
    @TableField("log_id")
    private String logId;
    @TableField("notice_id")
    private String noticeId;
    @TableField("user_id")
    private String userId;
    @TableField("is_read")
    private Integer isRead;
    @TableField("read_time")
    private Date readTime;




}
