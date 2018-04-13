package com.yunjing.approval.model.dto;

import lombok.Data;
import retrofit2.http.Field;

import java.io.Serializable;
import java.util.List;

/**
 * @version: 1.0.0
 * @author: liuxiaopeng
 * @date: 2018/4/10 10:53
 * @description:
 */
@Data
public class OrgMemberMessage implements Serializable {

    /**
     * 公司id
     */
    String companyId;

    /**
     * 头像颜色
     */
    String color;

    /**
     * 成员id
     */
    String memberId;


    /**
     * 成员名称
     */
    String memberName;

    /**
     * 成员头像
     */
    String profile;

    /**
     * 成员手机号
     */
    String mobile;

    /**
     * 成员的所在部门
     */
    List<String> deptIds;

    /**
     * 成员的所在部门名称
     */
    List<String> deptNames;

    /**
     * 成员的职位
     */
    String position;

    /**
     * 消息类型枚举
     */
    MessageType messageType;

    /**
     * 消息类型枚举
     */
    public enum MessageType {

        /**
         * 添加
         */
        INSERT,

        /**
         * 修改
         */
        MODIFY,

        /**
         * 删除
         */
        DELETE;

    }
}
