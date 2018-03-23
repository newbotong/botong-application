package com.yunjing.notice.body;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * <p> 地理位置收藏
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/20.
 */
@Data
public class NoticeDetailBody implements Serializable{

    /**
     * 公告id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 作者
     */
    private String author;
    /**
     * 创建日期
     */
    private Long createTime;
    /**
     * 封面图
     */
    private String cover;
    /**
     * 已读人数
     */
    private Integer readNumber;
    /**
     * 未读人数
     */
    private Integer notReadNumber;
    /**
     * 内容
     */
    private String content;
    /**
     * 图片地址
     */
    private String picture;
    /**
     * 图片名称
     */
    private String pictureName;
    /**
     * 发布人名称
     */
    private String issueUserName;
    /**
     * H5分享地址
     */
    private String noticeH5Address;
    /**
     * 保密状态 0为保密 1为不保密
     */
    private Integer secrecyState;
}
