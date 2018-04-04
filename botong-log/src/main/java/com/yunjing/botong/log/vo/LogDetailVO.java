package com.yunjing.botong.log.vo;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * <p> 日志列表明细vo对象
 * </p>
 *
 * @author jingwj
 * @since 2018/3/28.
 */
@Data
public class LogDetailVO {

    /**
     * 发送人信息
     */
    private  UserVO user;

    /**
     * 日志Id
     */
    @NotNullOrEmpty
    private String logId;

    /**
     * 日志头像
     */
    private String[] logImages;

    /**
     * 备注
     */
    private String remark;

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
    private List<LogConentVO> contents;

    /**
     * 发送人
     */
    private List<MemberVO> sendToUsers;

    /**
     * 已读人
     */
    private List<MemberVO> readUsers;

    /**
     * 未读人
     */
    private List<MemberVO> unReadUsers;

    /**
     * submitTIme
     */
    private Long submitTime;

    /**
     * 删除状态
     */
    private Integer deleteStatus;
}