package com.yunjing.botong.log.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import com.common.mybatis.utils.Constant;
import lombok.Data;

/**
 * <p>
 * <p>提醒
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@Data
@TableName("remind")
public class RemindEntity extends BaseModel<RemindEntity> {

    /**
     * 用户所在企业成员id
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 应用id
     */
    @TableField("app_id")
    private String appId;

    /**
     * 提醒开关（0-关闭，1-打开）
     */
    @TableField("remind_switch")
    private Integer remindSwitch;

    /**
     * 提交类型周期（1每天 2 每周 3 每月 4 季度 5 年度）
     */
    @TableField("submit_type")
    private Integer submitType;

    /**
     * 提醒周期（日 01:30，周 1-7，月 7号，一次发送：2018-03-23）
     */
    private String cycle;

    /**
     * 周期类型（day:日 周：week 月：month 一次发送：once）
     */
    @TableField("cycle_type")
    private String cycleType;

    /**
     * 提醒时间（如 15:20）
     */
    @TableField("job_time")
    private String jobTime;

    /**
     * 提醒方式（1-应用内推送，2-短信，3-dang）
     */
    @TableField("remind_mode")
    private Integer remindMode;

    /**
     * 是否是管理员 0-不是，1-是
     */
    @TableField("is_manager")
    private Integer isManager;

    /**
     * 是否删除标识
     */
    @TableField("is_delete")
    private Integer isDelete;

    public void beforeInsert() {
        this.setCreateTime(System.currentTimeMillis());
        this.setUpdateTime(this.getCreateTime());
        this.setIsDelete(Constant.IS_DELETE_0);
    }
}
