package com.yunjing.botong.log.params;

import lombok.Data;

import java.util.List;

/**
 * 日志模板参数
 *
 * @author 王开亮
 * @date 2018/3/30 16:17
 */
@Data
public class LogTemplateParam {

    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板图片
     */
    private String iconImage;
    /**
     * 所属机构ID
     */
    private long orgId;
    /**
     * 提交周期（1每天 2 每周 3 每月 4 季度 5 年度）
     */
    private int submitType;
    /**
     * 排序序号
     */
    private int sort;
    /**
     * 模板字段明细
     */
    private List<LogTemplateFieldParam> details;

}
