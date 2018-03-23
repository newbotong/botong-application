package com.yunjing.approval.model.vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 导出记录
 * @author 刘小鹏
 * @date 2018/01/15
 */
@Data
public class ExportLogVO {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 导出人员
     */
    private String name;

    /**
     * 记录时间
     */
    private Long createTime;

    /**
     * 审批类型
     */
    private String approvalType;

}
