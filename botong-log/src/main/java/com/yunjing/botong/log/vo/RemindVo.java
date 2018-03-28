package com.yunjing.botong.log.vo;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

/**
 * <p>
 * <p> 提醒vo对象
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@Data
public class RemindVo {

    /**
     * 用户所有企业的成员id
     */
    @NotNullOrEmpty
    private Long memberId;

    /**
     * 提醒开关（0-关闭，1-打开）
     */
    @NotNullOrEmpty
    private int remindSwitch;

    /**
     * 日报模版类型（1-日报 2-周报 3-月报）
     */
    @NotNullOrEmpty
    private int submitType;

    /**
     * 提醒周期（日 01:30，周 1-7，月 7号，一次发送：2018-03-23）
     */
    @NotNullOrEmpty
    private String cycle;

    /**
     * 周期类型（day:日 周：week 月：month 一次发送：once）
     */
    @NotNullOrEmpty
    private String cycleType;

    /**
     * 提醒时间（如 15:20），如果是提醒周期类型为day，则保证和cycle一致
     */
    @NotNullOrEmpty
    private String jobTime;

    /**
     * 提醒方式（0-应用内推送，1-短信，2-dang）
     */
    @NotNullOrEmpty
    private String remindMode;
}
