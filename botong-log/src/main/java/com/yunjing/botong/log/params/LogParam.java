package com.yunjing.botong.log.params;

import lombok.Data;

import java.util.List;

/**
 * 发布日志参数
 *
 * @author  王开亮
 * @date 2018/4/9 9:26
 */
@Data
public class LogParam {

    /**
     * 用户ID
     */
    private Long memberId;
    /**
     * 模板ID
     */
    private Long templateId;
    /**
     * 日志内容
     */
    private List<LogItemParam> logValues;
    /**
     * 图片列表
     */
    private List<String> images;
    /**
     * 接收人ID列表
     */
    private List<Long> sendToUser;
    /**
     * 接收群组ID列表
     */
    private List<Long> sendToGroup;
    /**
     * 备注
     */
    private String remark;

}
