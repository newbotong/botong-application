package com.yunjing.approval.web;

import com.yunjing.approval.model.dto.*;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.approval.service.IDataTransferService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 审批rpc服务
 *
 * @author 刘小鹏
 * @date 2018/03/21
 */
@RestController
@RequestMapping("/rpc/approval")
public class ApprovalRpcController extends BaseController {

    @Autowired
    private IDataTransferService transferService;

    /**
     * 保存botong1.0 approval 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-approval")
    public ResponseEntityWrapper addApproval(@RequestBody List<ApprovalDTO> dtoList) throws Exception {
        return success(transferService.addApproval(dtoList));
    }

    /**
     * 保存botong1.0 copy 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-copy")
    public ResponseEntityWrapper addCopy(@RequestBody List<CopyDTO> dtoList) throws Exception {
        return success(transferService.addCopy(dtoList));
    }

    /**
     * 保存botong1.0 copys 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-copys")
    public ResponseEntityWrapper addCopyS(@RequestBody List<CopySDTO> dtoList) throws Exception {
        return success(transferService.addCopyS(dtoList));
    }

    /**
     * 保存botong1.0 approval_sets_process 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-setprocess")
    public ResponseEntityWrapper addSetsProcess(@RequestBody List<SetsProcessDTO> dtoList) throws Exception {
        return success(transferService.addSetsProcess(dtoList));
    }

    /**
     * 保存botong1.0 approval_sets_condition 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-setprocess")
    public ResponseEntityWrapper addSetsCondition(@RequestBody List<SetsConditionDTO> dtoList) throws Exception {
        return success(transferService.addSetsCondition(dtoList));
    }

    /**
     * 保存botong1.0 approval_process 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-process")
    public ResponseEntityWrapper addProcess(@RequestBody List<ApprovalProcessDTO> dtoList) throws Exception {
        return success(transferService.addApprovalProcess(dtoList));
    }

    /**
     * 保存botong1.0 model 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-model")
    public ResponseEntityWrapper addModel(@RequestBody List<ModelDTO> dtoList) throws Exception {
        return success(transferService.addModel(dtoList));
    }
    /**
     * 保存botong1.0 model_item 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-modelitem")
    public ResponseEntityWrapper addModelItem(@RequestBody List<ModelItemDTO> dtoList) throws Exception {
        return success(transferService.addModelItem(dtoList));
    }

    /**
     * 保存botong1.0 model_item 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return ResponseEntityWrapper
     */
    @PostMapping("/save-orgmodel")
    public ResponseEntityWrapper addOrgModel(@RequestBody List<OrgModelDTO> dtoList) throws Exception {
        return success(transferService.addOrgModel(dtoList));
    }
}
