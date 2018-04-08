package com.yunjing.approval.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 企业信息
 * @author  刘小鹏
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org")
public class Org extends Model<Org> {

    /**
     * 企业ID
     */
    @TableId("org_id")
    private String orgId;

    /**
     * 所属集团编号
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 子公司序号
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 企业名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 企业简介
     */
    @TableField("org_desc")
    private String orgDesc;

    /**
     * 企业logo
     */
    @TableField("org_logo")
    private String orgLogo;

    /**
     * 企业开机启动图
     */
    @TableField("org_image")
    private String orgImage;

    /**
     * 启动图跳转链接
     */
    @TableField("org_url")
    private String orgUrl;

    /**
     * 起始时间
     */
    @TableField("start_time")
    private Timestamp startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private Timestamp endTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 管理员主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 行业类型
     */
    @TableField("org_type")
    private String orgType;

    /**
     * 企业地区
     */
    @TableField("org_area")
    private String orgArea;

    /**
     * 企业群组
     */
    @TableField("groups_org")
    private String groupsOrg;

    /**
     * 是否开启团队邀请
     */
    @TableField("is_open_invite")
    private int isOpenInvite;

    @Override
    protected Serializable pkVal() {
        return this.orgId;
    }
}
