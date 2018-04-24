package com.yunjing.approval.common;

/**
 * @author 裴志鹏
 * @date 2018/3/26 10:24
 */
public interface ContactsCommon {
    /**
     * 公司redis key
     */
    String ORG_COMPANY_KEY = "botong:org:company";

    /**
     * 部门redis key
     */
    String ORG_DEPT_KEY = "botong:org:dept";

    /**
     * 公司-部门 redis key
     */
    String ORG_COMPANY_DEPT_KEY = "botong:org:company:dept:";

    /**
     * 部门-子部门 redis key
     */
    String ORG_DEPT_SUBDEPT_KEY = "botong:org:dept:subdept:";

    /**
     * 部门-成员 redis key
     */
    String ORG_DEPT_MEMBER_KEY = "botong:org:dept:member:";

    /**
     * 成员 redis key
     */
    String ORG_MEMBER_KEY = "botong:org:member";

    /**
     * 用户 redis key
     */
    String ORG_USER_KEY = "botong:org:user";

    /**
     * 用户关心 redis key
     */
    String ORG_PASSPORT_CARE_FOR_KEY = "botong:org:passport:carefor:";

    /**
     * 公共状态:0
     */
    String COMMON_0 = "0";

    /**
     * 公共状态:1
     */
    String COMMON_1 = "1";

    /**
     * 公共状态:2
     */
    String COMMON_2 = "2";

    /**
     * 公共状态:3
     */
    String COMMON_3 = "3";

    /**
     * 手机号正则
     */
    String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$";

    /**
     * 空字符串
     */
    String EMPTY_VALUE = " ";
}
