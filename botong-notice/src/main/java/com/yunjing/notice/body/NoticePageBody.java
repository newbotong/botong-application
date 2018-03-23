package com.yunjing.notice.body;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 李双喜
 * @date 2018/3/21 17:17
 */
@Data
public class NoticePageBody implements Serializable{
    /**
     * 公告id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 创建日期
     */
    private Long createTime;
    /**
     * 作者
     */
    private String author;
    /**
     * 是否可删除 0不可删除 1可删除
     */
    private Integer whetherDelete;
    /**
     * 封页图
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

}
