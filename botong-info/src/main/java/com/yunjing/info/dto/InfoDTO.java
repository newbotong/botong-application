package com.yunjing.info.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 李双喜
 * @date 2018/4/3 10:26
 */
@Data
public class InfoDTO implements Serializable{
    /**
     * 资讯id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 图片地址
     */
    private String pictureUrl;
    /**
     * 创建时间
     */
    private Long createTime;
}
