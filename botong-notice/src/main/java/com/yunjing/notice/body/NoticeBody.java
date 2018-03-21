package com.yunjing.notice.body;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * <p> 公告
 * </p>
 *
 * @author 李双喜
 * @since 2018/3/20.
 */
@Data
public class NoticeBody implements Serializable {

    /**
     * 发布人用户id
     */
    private String issueUserId;

    /**
     * 封面图地址
     */
    private String cover;

    /**
     * 图片地址 ，多个图片逗号隔开
     */
    private String picture;

    /**
     * 图片名称 多个图片名称逗号隔开
     */
    private String pictureName;

    /**
     * 多个用户Id,用逗号隔开
     */
    private String userIds;

    /**
     * 保密状态 0为保密 1为不保密
     */
    private Integer secrecyState;

    /**
     * Dang的状态 0为Dang 1为非Dang
     */
    private String dangState;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告作者
     */
    private String author;

}
