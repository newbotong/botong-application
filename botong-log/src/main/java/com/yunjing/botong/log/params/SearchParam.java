package com.yunjing.botong.log.params;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

/**
 * 查询列表
 *
 * @author  jingwj
 * @date 2018/4/9 16:25
 */
@Data
public class SearchParam {

    /**
     * 成员id
     *
     */
    private String memberId;

    /**
     * 成员ids
     */
    private String[] userIds;

    /**
     * 部门ids
     */
    private String[] deptIds;

    /**
     * 日志ids
     */
    private String[] logIds;

    /**
     * 组织Id
     */
    @NotNullOrEmpty(message = "企业编码不能为空")
    private String orgId;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 开始时间
     */
    private String endDate;

    /**
     * 提交类型，日报，周报。月报
     */
    private Integer submitType;

    private Integer pageSize;

    private Integer pageNo;

    /**
     * 应用id
     */
    private String appId;

}
