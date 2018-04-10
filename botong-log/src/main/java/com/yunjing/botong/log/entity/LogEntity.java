package com.yunjing.botong.log.entity;

import com.yunjing.botong.log.params.LogParam;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 日志实体类
 * @author 王开亮
 * @date 2018/4/9 11:01
 */
@Data
public class LogEntity {


    /**
     * LogId
     */
    private Long logId;
    /**
     * 用户ID
     */
    private Long memberId;
    /**
     * 机构ID
     */
    private Long orgId;
    /**
     * 模板ID
     */
    private Long templateId;
    /**
     * 提交周期 1每天 2 每周 3 每月 4 季度 5 年度
     */
    private Integer submitType;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 日志正文
     */
    private List<LogItemEntity> logContent;
    /**
     * 日志附加图片
     */
    private List<String> logImages;
    /**
     * 发送给UserId
     */
    private List<String> sendToUserId;
    /**
     * 已读UserID
     */
    private List<String> readUserId;
    /**
     * 未读UserID
     */
    private List<String> unreadUserId;
    /**
     * 发送群组ID列表
     */
    private List<String> sendToGroupId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 发送时间
     */
    private Date submitTime;
    /**
     * 删除标识
     */
    private Boolean deleteStatus;

    public void fromParam(LogParam logParam) {

    }
}
