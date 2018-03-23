package com.yunjing.notice.body;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 李双喜
 * @date 2018/3/21 17:51
 */
@Data
public class NoticeDetailCBody implements Serializable{
    /**
     * id
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
     * 创建时间
     */
    private Long createTime;
    /**
     * 封页
     */
    private String cover;
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
}
