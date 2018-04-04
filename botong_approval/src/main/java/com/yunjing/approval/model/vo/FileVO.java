package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * 附件
 *
 * @author liuxiaopeng
 * @date 2018/04/04
 */
@Data
public class FileVO {

    public FileVO() {

    }

    public FileVO(String name, String size, String url) {
        this.name = name;
        this.size = size;
        this.url = url;
    }

    private String name;
    private String size;
    private String url;

}
