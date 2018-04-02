package com.yunjing.botong.log.vo;

/**
 * 封装 日志模板 列表VO
 * @author  王开亮
 * @date 2018/3/30 16:51
 */
public class LogTemplateItem {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 图标URL
     */
    private String iconImage;
    /**
     * 提交周期（1每天 2 每周 3 每月 4 季度 5 年度）
     */
    private Long submitType;

}
