package com.yunjing.notice.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import com.yunjing.notice.common.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 公告用户关系表
 *
 * @author 李双喜
 * @since 2018/03/20/.
 */
@Data
@TableName("notice_user")
public class NoticeUserEntity extends BaseEntity<NoticeUserEntity> {
    private static final long serialVersionUID = 1L;

    /**
     * 公告id
     */
    @TableField("notice_id")
    private Long noticeId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 是否阅读 0为已读 1为未读
     */
    @TableField("state")
    private Integer state;

}
