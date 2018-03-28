package com.yunjing.approval.api;

import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 客户端-审批服务接口
 *
 * @author 刘小鹏
 * @date 2018/03/22
 */
@RestController
@RequestMapping("/approval")
public class ClientApprovalOperationController extends BaseController {

    @Autowired
    private IApprovalApiService approvalApiService;

    @Autowired
    private IModelItemService modelItemService;
    @Autowired
    private IApprovalService approvalService;

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
     * 审批同意操作
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @param state      审批状态
     * @param remark     备注信息
     * @return
     * @throws Exception
     */
    @PostMapping("/agree")
    public ResponseEntityWrapper agree(@RequestParam("oid") Long oid,
                                       @RequestParam("uid") Long uid,
                                       @RequestParam("approvalId") Long approvalId,
                                       @RequestParam("state") Integer state,
                                       @RequestParam("remark") String remark) throws Exception {
        return success();
    }

    /**
     * 审批拒绝操作
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @param state      审批状态
     * @param remark     备注信息
     * @return
     * @throws Exception
     */
    @PostMapping("/refuse")
    public ResponseEntityWrapper refuse(@RequestParam("oid") Long oid,
                                        @RequestParam("uid") Long uid,
                                        @RequestParam("approvalId") Long approvalId,
                                        @RequestParam("state") Integer state,
                                        @RequestParam("remark") String remark) throws Exception {
        return success();
    }

    /**
     * 审批撤销操作
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @param state      审批状态
     * @param remark     备注信息
     * @return
     * @throws Exception
     */
    @PostMapping("/revoke")
    public ResponseEntityWrapper revoke(@RequestParam("oid") Long oid,
                                        @RequestParam("uid") Long uid,
                                        @RequestParam("approvalId") Long approvalId,
                                        @RequestParam("state") Integer state,
                                        @RequestParam("remark") String remark) throws Exception {
        return success();
    }

    /**
     * 审批转让操作
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @param state      审批状态
     * @param remark     备注信息
     * @return
     * @throws Exception
     */
    @PostMapping("/transfer")
    public ResponseEntityWrapper transfer(@RequestParam("oid") Long oid,
                                          @RequestParam("uid") Long uid,
                                          @RequestParam("approvalId") Long approvalId,
                                          @RequestParam("state") Integer state,
                                          @RequestParam("remark") String remark) throws Exception {
        return success();
    }
}
