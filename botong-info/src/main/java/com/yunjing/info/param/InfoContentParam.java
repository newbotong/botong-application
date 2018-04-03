package com.yunjing.info.param;

import com.yunjing.mommon.validate.annotation.Length;
import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * 资讯内容新增入参
 *
 * @author 李双喜
 * @date 2018/3/30 17:29
 */
@Data
public class InfoContentParam implements Serializable {
    /**
     * 企业id
     */
    @NotNullOrEmpty
    private Long orgId;

    /**
     * 类目id
     */
    @NotNullOrEmpty
    private Long catalogId;

    /**
     * 标题
     */
    @NotNullOrEmpty
    @Length(message = "标题不得超过35个字")
    private String title;

    /**
     * 内容
     */
    @Length(message = "内容不得超过2500字")
    @NotNullOrEmpty
    private String content;

    /**
     *
     */
}
