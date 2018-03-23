package com.yunjing.approval.model.vo;


import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liuxiaopeng
 *
 */
@Data
public class ApprovalContent {
        private String approvalId;
        private String userId;
        private String userNick;
        private String userAvatar;
        private int state;
        private Timestamp createTime;
        private String modelName;
        private String color;
        private String datetime;
        private int rovastate;
        private String msg;
}
