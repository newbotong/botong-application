package com.yunjing.notice.common;

/**
 * 公告常量
 *
 * @author 李双喜
 * @date 2018/3/21 13:45
 */
public interface NoticeConstant {

    /**
     * 是否逻辑删除（0正常 1删除）
     */
    Integer LOGIC_DELETE_NOMAL = 0;
    Integer LOGIC_DELETE_DELETE = 1;

    /**
     * 是否已读  是否阅读 0为已读 1为未读
     */
    Integer NOTICE_READ = 0;
    Integer NOTICE_NOT_READ = 1;

    String USER_INFO_REDIS = "botong:org:user";
}
