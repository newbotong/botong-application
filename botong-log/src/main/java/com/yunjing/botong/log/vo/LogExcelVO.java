package com.yunjing.botong.log.vo;

import lombok.Data;

import java.util.List;

/**
 * 导出数据视图
 * @author liuxiaopeng
 * @date 2018/01/24
 */

@Data
public class LogExcelVO {

    /**
     * 姓名
     */
    private String sender;

    /**
     * 部门
     */
    private String deptName;

    /**
     * 发送时间
     */
    private String sendTime;

    /**
     * 日志类型
     */
    private String type;

    /**
     * 日志主键
     */
    private String logId;

    /**
     * 其他
     */
    private List<AttrValueVO> listValue;

}
