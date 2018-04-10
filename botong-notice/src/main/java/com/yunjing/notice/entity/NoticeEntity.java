package com.yunjing.notice.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 公告内容实体
 *
 * @author 李双喜
 * @since 2018/03/20/.
 */
@Data
@TableName("notice")
public class NoticeEntity extends BaseModel<NoticeEntity> {
    private static final long serialVersionUID = 1L;

    /**
     * 发布人用户id
     */
    @TableField("issue_user_id")
    private String issueUserId;


    /**
     * 公告标题
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容
     */
    @TableField("content")
    private String content;

    /**
     * 公告封面图
     */
    @TableField("cover")
    private String cover;

    /**
     * 图片地址 多个地址逗号隔开
     */
    @TableField("picture")
    private String picture;

    /**
     * 作者
     */
    @TableField("author")
    private String author;

    /**
     * 保密状态 0为保密 1为不保密
     */
    @TableField("secrecy_state")
    private Integer secrecyState;

    /**
     * Dang的状态 0为Dang 1为非Dang
     */
    @TableField("dang_state")
    private Integer dangState;

    /**
     * 图片名称，多个名称逗号隔开
     */
    @TableField("picture_name")
    private String pictureName;

    /**
     * 逻辑删除 0正常  1删除
     */
    @TableLogic
    @TableField("logic_delete")
    private Integer logicDelete;

    /**
     * 已读人数
     */
    @TableField("read_num")
    private Integer readNum;

    /**
     * 未读人数
     */
    @TableField("not_read_num")
    private Integer notReadNum;
}
