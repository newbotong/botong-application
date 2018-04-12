package com.yunjing.approval.api;

import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 客户端-审批服务接口
 *
 * @author 刘小鹏
 * @date 2018/03/22
 */
@RestController
@RequestMapping(value = "/approval")
public class ClientApprovalOperationController extends BaseController {

    @Autowired
    private IApprovalApiService approvalApiService;
    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private IApprovalService approvalService;

    @PostMapping("/get-member-list")
    public ResponseEntityWrapper getMember(@RequestParam("companyId") String companyId) throws Exception {

        return success(approvalUserService.addUser(companyId));
    }


    /**
     * 提交审批信息
     *
     * @param companyId   公司id
     * @param memberId    成员id
     * @param modelId     模型主键
     * @param deptId      部门主键
     * @param jsonData    要提交的审批数据
     * @param sendUserIds 要推送的审批人主键，多个以英文，隔开
     * @param sendCopyIds 要推送的抄送人主键，多个以英文，隔开
     * @return
     * @throws Exception
     */
    @PostMapping("/submit")
    public ResponseEntityWrapper submit(@RequestParam("companyId") String companyId,
                                        @RequestParam("memberId") String memberId,
                                        @RequestParam("modelId") String modelId,
                                        @RequestParam("jsonData") String jsonData,
                                        @RequestParam(value = "deptId",required = false) String deptId,
                                        @RequestParam("sendUserIds") String sendUserIds,
                                        @RequestParam(value = "sendCopyIds", required = false) String sendCopyIds) throws Exception {
        return success(approvalService.submit(companyId, memberId, modelId, jsonData, sendUserIds, sendCopyIds));
    }

    /**
     * 审批处理操作
     *
     * @param companyId  公司id
     * @param memberId   成员id
     * @param approvalId 审批主键
     * @param state      审批状态
     * @return
     * @throws Exception
     */
    @PostMapping("/solve")
    public ResponseEntityWrapper solve(@RequestParam("companyId") String companyId,
                                       @RequestParam("memberId") String memberId,
                                       @RequestParam("approvalId") String approvalId,
                                       @RequestParam("state") Integer state) throws Exception {
        return success(approvalApiService.solveApproval(companyId, memberId, approvalId, state));
    }

    /**
     * 审批撤销操作
     *
     * @param companyId  公司id
     * @param memberId   成员id
     * @param approvalId 审批主键
     * @return
     * @throws Exception
     */
    @PostMapping("/revoke")
    public ResponseEntityWrapper revoke(@RequestParam("companyId") String companyId,
                                        @RequestParam("memberId") String memberId,
                                        @RequestParam("approvalId") String approvalId) throws Exception {
        return success(approvalApiService.revokeApproval(companyId, memberId, approvalId));
    }

    /**
     * 审批转让操作
     *
     * @param companyId  公司id
     * @param memberId   成员id
     * @param approvalId 审批主键
     * @return
     * @throws Exception
     */
    @PostMapping("/transfer")
    public ResponseEntityWrapper transfer(@RequestParam("companyId") String companyId,
                                          @RequestParam("memberId") String memberId,
                                          @RequestParam("userId") String userId,
                                          @RequestParam("approvalId") String approvalId) throws Exception {
        return success(approvalApiService.transferApproval(companyId, memberId, userId, approvalId));
    }

    /**
     * 修改抄送列表已读状态
     *
     * @param approvalId 审批主键数组
     * @return ResponseEntityWrapper
     * @throws Exception 抛异常
     */
    @PostMapping("/update-is-read")
    public ResponseEntityWrapper updateCopyReadState(@RequestParam("approvalId") String[] approvalId) throws Exception {
        return success(approvalApiService.updateCopyReadState(approvalId));
    }

    /**
     * 审批发送dang提醒
     *
     * @param companyId
     * @param memberId
     * @param message
     * @return
     * @throws Exception
     */
    @PostMapping("/send-dang")
    public ResponseEntityWrapper sendApprovalDang(@RequestParam("companyId") String companyId,
                                                  @RequestParam("memberId") String memberId,
                                                  @RequestParam("message") String message) throws Exception {
        return success();
    }
}
