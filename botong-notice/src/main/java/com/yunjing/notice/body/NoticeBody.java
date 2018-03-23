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
    private Long issueUserId;

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
     * 多个用户属性,用逗号隔开
     */
    private String userInfo;

    /**
     * 保密状态 0为保密 1为不保密
     */
    private Integer secrecyState;

    /**
     * Dang的状态 0为Dang 1为非Dang
     */
    private Integer dangState;

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

    /**
     * 发布人的头像地址
     */
    private String img;

    /**
     * 发布人的名称
     */
    private String name;

}
