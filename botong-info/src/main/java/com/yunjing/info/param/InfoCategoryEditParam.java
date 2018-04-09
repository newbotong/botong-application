package com.yunjing.info.param;

import com.yunjing.mommon.validate.annotation.Length;
import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李双喜
 * @date 2018/4/9 14:34
 */
@Data
public class InfoCategoryEditParam implements Serializable {
    /**
     * 资讯id
     */
    @NotNullOrEmpty(message = "id不能为空")
    private String id;

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
