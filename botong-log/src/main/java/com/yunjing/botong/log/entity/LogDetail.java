package com.yunjing.botong.log.entity;

import com.yunjing.botong.log.vo.LogConentVO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @version: 1.0.0
 * @author: jingwj
 * @date: 2018/3/27 8:49
 * @description:
 */
@Data
public class LogDetail {

    /**
     * 发送人信息
     */

    private String memberId;

    /**
     * 日志Id
     */
    @Field("_id")
    private String logId;

    /**
     * 模板id
     */
    private Long templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 企业Id
     */
    private Long orgId;

    /**
     * 日志头像
     */
    private String[] logImages;

    /**
     * 备注
     */
    private String remark;

    /**
     * 日志状态，
     */
    private String state;

    /**
     * 日志版本
     */
    private String logVersion;

    /**
     * 日志内容
     */
    @DBRef
    private List<LogConentVO> contents;

    /**
     * 发送人
     */
    private Set<String> sendToUserId;

    /**
     * 已读人
     */
    private Set<String> readUserId;

    /**
     * 未读人
     */
    private Set<String> unreadUserId;

    /**
     * submitTIme
     */
    private Date submitTime;

    /**
     * 删除状态
     */
    private Integer deleteStatus;
}
