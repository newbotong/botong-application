package com.yunjing.approval.param;

import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;


/**
 * 审批筛选参数
 *
 * @author 刘小鹏
 * @date 2018/03/29
 */
@Data
public class DataParam {

    /** 多个成员ID 以逗号隔开 */
    private String[] memberIds;

    /** 多个部门ID 以逗号隔开 */
    private String[] deptIds;

    /** 当前页数 */
    private Integer currentPage;

    /** 每页显示条数 */
    private Integer pageSize;

    @NotNullOrEmpty(message = "公司id不能为空")
    private String companyId;

    @NotNullOrEmpty(message = "成员id不能为空")
    private String memberId;

    /** 模型主键, 审批类型, 可空(全部) */
    private String modelId;

    /** 审批状态  0:审批中 1:审批完成 2:已撤回, 可空(全部)  */
    private Integer state;

    /** 审批标题, 可空  */
    private String title;

    /** 发起时间_开始, 可空  */
    private Long createTimeStart;

    /** 发起时间_结束, 可空 */
    private Long createTimeEnd;

    /** 完成时间_开始, 可空  */
    private Long finishTimeStart;

    /** 完成时间_结束, 可空  */
    private Long finishTimeEnd;

}
