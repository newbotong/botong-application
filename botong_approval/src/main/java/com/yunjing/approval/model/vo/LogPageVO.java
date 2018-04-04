package com.yunjing.approval.model.vo;

import com.baomidou.mybatisplus.plugins.Page;
import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/04/04
 */
@Data
public class LogPageVO {

    public LogPageVO() {

    }

    public LogPageVO(Page page) {
        this.current = page.getCurrent();
        this.pages = page.getPages();
        this.size = page.getSize();
        this.total = page.getTotal();
    }

    private Integer current;

    private Integer pages;

    private Integer size;

    private Integer total;

    private List<ExportLogVO> records;
}
