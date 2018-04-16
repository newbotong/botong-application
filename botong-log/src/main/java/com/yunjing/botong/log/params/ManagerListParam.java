package com.yunjing.botong.log.params;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * <p> 管理列表参数
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/9.
 */
@Data
public class ManagerListParam implements Serializable {

    /**
     * 管理员id
     */
    private String memberId;

    /**
     * 组织机构id
     */
    private String orgId;

    /**
     * 提交类型（1-日报 2-周报 3-月报）
     */
    private int submitType;

    /**
     * 时间
     */
    private String date;

    /**
     * 页码
     */
    private int pageNo;

    /**
     * 页大小
     */
    private int pageSize;
}
