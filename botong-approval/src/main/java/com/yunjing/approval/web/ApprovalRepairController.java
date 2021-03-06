package com.yunjing.approval.web;

import com.yunjing.approval.service.IApprovalRepairService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批数据修复
 *
 * @author 刘小鹏
 * @date 2018/1/15
 */
@RestController
@RequestMapping("/approval/repair")
public class ApprovalRepairController extends BaseController {

    @Autowired
    private IApprovalRepairService approvalRepairService;

    /**
     * 修复审批标题数据
     *
     * @param companyId 公司id
     * @return 审批列表
     */
    @PostMapping("/title")
    public ResponseEntityWrapper title(@RequestParam("companyId") String companyId) {
        return success(approvalRepairService.repairTitle(companyId));
    }

    /**
     * 修复审批完成时间数据
     *
     * @param companyId 企业主键
     * @return 审批列表
     */
    @PostMapping("/finish")
    public ResponseEntityWrapper finish(@RequestParam("companyId") String companyId) {
        return success(approvalRepairService.repairFinishTime(companyId));
    }
    /**
     * 修复审批所属部门/版本号/审批详情数据
     *
     * @param companyId 企业主键
     * @return 审批列表
     */
    @PostMapping("/dept")
    public ResponseEntityWrapper repairDeptId(@RequestParam("companyId") String companyId) {
        return success(approvalRepairService.repairDeptId(companyId));
    }

}
