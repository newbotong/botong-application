package com.yunjing.approval.param;

import lombok.Data;

/**
 * 审批筛选参数
 *
 * @author 刘小鹏
 * @date 2018/03/29
 */
@Data
public class FilterParam {

    /**
     * 审批状态
     */
    private Integer state;

    /**
     * 审批结果
     */
    private Integer result;

    /**
     * 审批类型
     */
    private String modelId;

    /**
     * 审批时间
     */
    private Long time;

    /**
     * 部门主键
     */
    private String deptId;

    /**
     * 搜索关键字
     */
    private String searchKey;

    @Override
    public String toString() {
        return "FilterParam{" +
                "state=" + state +
                ", result=" + result +
                ", modelId=" + modelId +
                ", time=" + time +
                ", deptId='" + deptId + '\'' +
                ", searchKey='" + searchKey + '\'' +
                '}';
    }
}
