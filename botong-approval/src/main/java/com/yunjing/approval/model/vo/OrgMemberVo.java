package com.yunjing.approval.model.vo;

import lombok.Data;

/**
 * 企业成员集合
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/3/30 9:06
 * @description
 **/
@Data
public class OrgMemberVo {

    /** 成员编号 */
    private String memberId;

    /** 成员头像 */
    private String faceUrl;

    /** 成员昵称 */
    private String nick;

    /** 企业编号 */
    private String orgId;

}
