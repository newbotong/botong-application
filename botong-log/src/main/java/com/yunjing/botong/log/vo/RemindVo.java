package com.yunjing.botong.log.vo;

import lombok.Data;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@Data
public class RemindVo {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 提醒开关
     */
    private int remindSwitch;

    /**
     * 日志类型
     */
    private int submitType;

    /**
     * 提醒周期
     */
    private String cycle;

    /**
     * 提醒类型（日周月）
     */
    private String cycleType;

    /**
     * 提醒时间
     */
    private String jobTime;

    /**
     * 提醒模式（应用内推送、短信、dang）
     */
    private String remindMode;
}
