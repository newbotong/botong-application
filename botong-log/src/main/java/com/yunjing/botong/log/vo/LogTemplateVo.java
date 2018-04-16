package com.yunjing.botong.log.vo;

import com.yunjing.botong.log.entity.LogTemplateEntity;
import lombok.Data;

import java.util.List;

/**
 * @author auth
 * @date 2018/4/4 9:11
 */
@Data
public class LogTemplateVo {

    /**
     * ID
     */
    private String id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板图标
     */
    private String image;

    /**
     * 机构ID
     */
    private String orgId;

    /**
     * 提交类型 1每天 2 每周 3 每月 4 季度 5 年度
     */
    private Integer submitType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否是当前模板
     */
    private Boolean currently;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 是否是用户
     */
    private Boolean userDefined;

    /**
     * 字段列表
     */
    private List<LogTemplateFieldVo> items;

    public void fromEntity(LogTemplateEntity entity){
        this.id = entity.getId();
        this.name = entity.getTemplateName();
        this.image = entity.getIconImage();
        this.orgId = entity.getOrgId();
        this.submitType = entity.getSubmitType();
        this.sort = entity.getSort();
        this.currently = entity.getCurrently();
        this.version = entity.getVersion();
        this.userDefined = entity.getUserDefined();
    }

}
