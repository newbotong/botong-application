package com.yunjing.approval.model.vo;

import com.yunjing.approval.model.entity.Approval;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author roc
 * @date 2018/01/15
 */
@Data
public class ApprovalVO {

    public ApprovalVO(Approval approval){
        this.id = approval.getId();
        this.title = approval.getTitle();
        this.createTime = approval.getCreateTime();
        this.finishTime = approval.getFinishTime();
        this.state = approval.getState();
        this.result = approval.getResult();
    }

    private Long id;

    private String title;

    private Long createTime;

    private Long finishTime;

    private String approver;

    private Integer state;

    private Integer result;
}
