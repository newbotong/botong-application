package com.yunjing.botong.log.params;

import lombok.Data;

/**
 * <p>
 * <p> 任务调度参数
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/29.
 */
@Data
public class SchedulerParam {

    /**
     * 任务id
     */
    private Long id;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 周期
     */
    private String cycle;

    /**
     * 周期类型
     */
    private String cycleType;

    /**
     * 提醒时间
     */
    private String jobTime;

    /**
     * 业务标识
     */
    private String outKey;

    /**
     * 消息内容
     */
    private String record;

    /**
     * 备注
     */
    private String remark;

    /**
     * 主题名称
     */
    private String jobTitle;
}
