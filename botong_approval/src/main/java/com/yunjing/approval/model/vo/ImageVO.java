package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * 图片
 *
 * @author lixiaopeng
 * @date 2018/04/04
 */
@Data
public class ImageVO {

    public ImageVO() {

    }

    public ImageVO(String url) {
        this.url = url;
    }

    private String url;

}
