package com.yunjing.botong.log.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * <p> 成员信息
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/30.
 */
@Data
public class MemberInfo {

    private String id;

    private String parentId;

    private String sort;

    private String orgType;

    private String name;

    private int createTime;

    private String childrenList;

    private String passportId;

    private String companyId;

    private String position;

    private String mobile;

    private String email;

    private List<String> deptIds;
}
