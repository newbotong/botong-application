package com.yunjing.info.param;

import com.yunjing.mommon.validate.annotation.Length;
import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import com.yunjing.mommon.validate.annotation.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 资讯新增目录入参
 *
 * @author 李双喜
 * @date 2018/3/30 16:04
 */
@Data
public class InfoCategoryParam implements Serializable {
    /**
     * 二级类目id
     */
    private String catalogId;
    /**
     * 一级类目id
     */
    @NotNullOrEmpty
    private String oneCatalogId;
    /**
     * 企业id
     */
    @NotNullOrEmpty
    private String orgId;
    /**
     * 部门名称
     */
    @NotNullOrEmpty
    private String departmentName;
    /**
     * 标题
     */
    @NotNullOrEmpty
    @Length(message = "不得超过20个字")
    private String title;
    /**
     * 封页图
     */
    private String pictureUrl;
    /**
     * 内容
     */
    @NotNullOrEmpty
    @Length(message = "内容不得超过2000字")
    private String content;
}
