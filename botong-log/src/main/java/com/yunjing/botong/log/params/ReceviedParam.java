package com.yunjing.botong.log.params;

import lombok.Data;

/**
 * 我收到的列表
 *
 * @author  jingwj
 * @date 2018/3/30 16:25
 */
@Data
public class ReceviedParam {

    /**
     * 人员Id
     */
    private Long userId;

    /**
     * 组织Id
     */
    private Long orgId;

    /**
     * 发送人集合，逗号间隔
     */
    private Long[] sendUserIds;

    /**
     * 未读已读
     */
    private String readStatus;

    private Integer pageSize;

    private Integer pageNo;

}
