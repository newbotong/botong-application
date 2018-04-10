package com.yunjing.botong.log.entity;

import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.yunjing.botong.log.params.LogParam;
import com.yunjing.botong.log.vo.LogConentVO;
import com.yunjing.botong.log.vo.LogTemplateVo;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

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
    @Field("logId")
    private String logId;

    /**
     * 模板id
     */
    private String templateId;
    /**
     * 提交周期 1每天 2 每周 3 每月 4 季度 5 年度
     */
    private Integer submitType;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 企业Id
     */
    private String orgId;

    /**
     * 日志头像
     */
    private List<String> logImages;

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
     * 发送群组ID列表
     */
    private Set<String> sendToGroupId;

    /**
     * submitTIme
     */
    private Date submitTime;

    /**
     * 删除状态
     */
    private Integer deleteStatus;


    public void fromParam(LogParam param, LogTemplateVo vo) {
        this.logId = IdWorker.get32UUID();
        this.submitType = vo.getSubmitType();
        this.submitTime = new Date();
        this.deleteStatus = 0;
        this.templateName = vo.getName();
        this.orgId = vo.getOrgId();

        this.memberId=param.getMemberId();
        this.templateId=param.getTemplateId();

        if(this.contents==null){
            this.contents = new ArrayList<>();
        }
        for (int i = 0; i < param.getLogValues().size(); i++) {
            LogConentVO itemEntity = new LogConentVO();
            itemEntity.fromParam(param.getLogValues().get(i),vo);
            this.contents.add(itemEntity);
        }
        this.logImages=param.getImages();
        this.sendToUserId=param.getSendToUser();
        this.unreadUserId=param.getSendToUser();
        this.readUserId = new HashSet<>();
        this.sendToGroupId=param.getSendToGroup();
        this.remark=param.getRemark();
    }
}
