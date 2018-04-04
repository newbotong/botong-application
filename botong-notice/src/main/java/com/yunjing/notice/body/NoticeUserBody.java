package com.yunjing.notice.body;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户公告返回参数
 *
 * @author 李双喜
 * @date 2018/3/21 17:34
 */
@Data
public class NoticeUserBody implements Serializable {
    /**
     * 用户名称
     */
    private String userName;
    /**
     * Img图片地址
     */
    private String img;
}
