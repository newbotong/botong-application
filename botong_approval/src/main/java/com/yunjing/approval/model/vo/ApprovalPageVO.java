package com.yunjing.approval.model.vo;

import com.baomidou.mybatisplus.plugins.Page;
import lombok.Data;

import java.util.List;

/**
 * @author roc
 * @date 2018/01/15
 */
@Data
public class ApprovalPageVO {
    public ApprovalPageVO(Page page) {
        this.current = page.getCurrent();
        this.pages = page.getPages();
        this.size = page.getSize();
        this.total = page.getTotal();
    }

    private Integer current;

    private Integer pages;

    private Integer size;

    private Integer total;

    private List<ApprovalVO> records;
}
