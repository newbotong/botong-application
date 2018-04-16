package com.yunjing.notice.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

import java.util.Date;

/**
 * @author 李双喜
 * @date 2018/4/11 9:43
 */
@Data
@TableName("notice_copy")
public class ExportNoticeEntity{
    /**
     *CREATE TABLE `notice_copy` (
     *   `notice_id` char(32) NOT NULL COMMENT '主键',
     *   `notice_type` int(11) NOT NULL COMMENT '1:通知 2:公告',
     *   `title` varchar(200) NOT NULL COMMENT '标题',
     *   `author` varchar(100) DEFAULT NULL COMMENT '作者',
     *   `content` text NOT NULL COMMENT '内容',
     *   `title_img` varchar(255) DEFAULT NULL COMMENT '封面图片',
     *   `send_time` datetime NOT NULL COMMENT '时间',
     *   `state` int(11) NOT NULL DEFAULT '0' COMMENT '0:正常 1:已删除',
     *   `user_id` char(32) NOT NULL COMMENT '公告创建人Id',
     *   `org_id` char(32) NOT NULL COMMENT '企业Id',
     *   `save_time` datetime DEFAULT NULL COMMENT '保存时间',
     *   `save_state` int(11) DEFAULT '0' COMMENT '2:发送 1:保存',
     *   PRIMARY KEY (`notice_id`),
     *   KEY `user_id` (`user_id`),
     *   KEY `org_id` (`org_id`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */
    @TableField("notice_id")
    private String noticeId;
    @TableField("notice_type")
    private Integer noticeType;
    @TableField("title")
    private String title;
    @TableField("author")
    private String author;
    @TableField("content")
    private String content;
    @TableField("title_img")
    private String titleImg;
    @TableField("send_time")
    private Date sendTime;
    @TableField("state")
    private Integer state;
    @TableField("user_id")
    private String userId;
    @TableField("org_id")
    private String orgId;
    @TableField("save_time")
    private Date saveTime;
    @TableField("save_state")
    private Integer saveState;




}
