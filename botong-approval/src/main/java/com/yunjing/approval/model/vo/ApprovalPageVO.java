package com.yunjing.approval.model.vo;

import com.baomidou.mybatisplus.plugins.Page;
import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/01/15
 */
@Data
public class ApprovalPageVO {
    public ApprovalPageVO(Page page) {
        this.currentPage = page.getCurrent();
        this.totalPage = page.getPages();
        this.pageSize = page.getSize();
        this.totalCount = page.getTotal();
    }

    private Integer currentPage;

    private Integer totalPage;

    private Integer pageSize;

    private Integer totalCount;

    private List<ApprovalVO> rows;
}
