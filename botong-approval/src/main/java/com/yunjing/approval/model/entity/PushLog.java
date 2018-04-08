package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推送记录
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Data
@TableName("push_log")
@EqualsAndHashCode(callSuper = true)
public class PushLog extends BaseModel<PushLog>{
    /**
     * 企业主键
     */
    @TableField("org_id")
    private Long orgId;

    /**
     * 用户主键
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 推送类型
     */
    @TableField("data_type")
    private Integer datatype;

    /**
     * 推送内容主键
     */
    @TableField("info_id")
    private Long infoId;

    /**
     * 推送消息
     */
    @TableField("message")
    private String message;

    /**
     * 是否推给抄送人
     */
    @TableField("copy_num")
    private Integer copyNum;

    /**
     * 消息类型
     */
    @TableField("state")
    private Integer state = 0;

    /**
     * 日志、评论内容
     */
    @TableField("log_content")
    private String logContent;
}
