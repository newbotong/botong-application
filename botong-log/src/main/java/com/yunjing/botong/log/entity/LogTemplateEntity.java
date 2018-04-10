package com.yunjing.botong.log.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import com.yunjing.botong.log.params.LogTemplateParam;
import lombok.Data;

/**
 * @author auth
 * @date 2018/4/2 11:22
 */
@Data
@TableName("log_template")
public class LogTemplateEntity extends BaseModel<LogTemplateEntity> {

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String  templateName;
    /**
     * 模板图片
     */
    @TableField("icon_image")
    private String  iconImage;
    /**
     * 所属机构ID
     */
    @TableField("org_id")
    private String  orgId;
    /**
     * 提交周期（1每天 2 每周 3 每月 4 季度 5 年度）
     */
    @TableField("submit_type")
    private Integer submitType;
    /**
     * 排序序号
     */
    private Integer sort;

    /**
     *是否是用户自定义的
     */
    @TableField("user_defined")
    private Boolean userDefined ;

    /**
     * 是否最新版本
     */
    private Boolean currently;
    /**
     * 版本
     */
    private Integer version;
    /**
     * 是否逻辑删除，1：删除，0：不删除
     */
    @TableField("deleted")
    private Boolean deleted;

    public void fromParam(LogTemplateParam logTemplateParam){
        this.templateName = logTemplateParam.getTemplateName();
        this.iconImage = logTemplateParam.getIconImage();
        this.orgId = logTemplateParam.getOrgId();
        this.submitType = logTemplateParam.getSubmitType();
        this.sort = logTemplateParam.getSort();
    }

}
