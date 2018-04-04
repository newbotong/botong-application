package com.yunjing.approval.api;

import com.yunjing.approval.processor.feign.DangFeign;
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
    @Autowired
    private DangFeign dangFeign;

    @PostMapping("/get-member-list")
    public ResponseEntityWrapper getMember(@RequestParam("oid") String oid,
                                           @RequestParam("uid") String uid) throws Exception {

        return success(approvalUserService.addUser(oid, uid));
    }


    /**
     * 提交审批信息
     *
     * @param oid         企业主键
     * @param uid         用户主键
     * @param modelId     模型主键
     * @param jsonData    要提交的审批数据
     * @param sendUserIds 要推送的审批人主键，多个以英文，隔开
     * @param sendCopyIds 要推送的抄送人主键，多个以英文，隔开
     * @return
     * @throws Exception
     */
    @PostMapping("/submit")
    public ResponseEntityWrapper submit(@RequestParam("oid") Long oid,
                                        @RequestParam("uid") Long uid,
                                        @RequestParam("modelId") Long modelId,
                                        @RequestParam("jsonData") String jsonData,
                                        @RequestParam("sendUserIds") String sendUserIds,
                                        @RequestParam("sendCopyIds") String sendCopyIds) throws Exception {
        return success(approvalService.submit(oid, uid, modelId, jsonData, sendUserIds, sendCopyIds));
    }

    /**
     * 审批处理操作
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @param state      审批状态
     * @return
     * @throws Exception
     */
    @PostMapping("/solve")
    public ResponseEntityWrapper solve(@RequestParam("oid") Long oid,
                                       @RequestParam("uid") Long uid,
                                       @RequestParam("approvalId") Long approvalId,
                                       @RequestParam("state") Integer state) throws Exception {
        return success(approvalApiService.solveApproval(oid, uid, approvalId, state));
    }

    /**
     * 审批撤销操作
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @return
     * @throws Exception
     */
    @PostMapping("/revoke")
    public ResponseEntityWrapper revoke(@RequestParam("oid") Long oid,
                                        @RequestParam("uid") Long uid,
                                        @RequestParam("approvalId") Long approvalId) throws Exception {
        return success(approvalApiService.revokeApproval(oid, uid, approvalId));
    }

    /**
     * 审批转让操作
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @return
     * @throws Exception
     */
    @PostMapping("/transfer")
    public ResponseEntityWrapper transfer(@RequestParam("oid") Long oid,
                                          @RequestParam("uid") Long uid,
                                          @RequestParam("userId") Long userId,
                                          @RequestParam("approvalId") Long approvalId) throws Exception {
        return success(approvalApiService.transferApproval(oid, uid, userId, approvalId));
    }

    /**
     * 审批发送dang提醒
     *
     * @param oid
     * @param uid
     * @param message
     * @return
     * @throws Exception
     */
    @PostMapping("/send-dang")
    public ResponseEntityWrapper sendApprovalDang(@RequestParam("oid") String oid,
                                                  @RequestParam("uid") String uid,
                                                  @RequestParam("message") String message) throws Exception {
        return success();
    }
}
