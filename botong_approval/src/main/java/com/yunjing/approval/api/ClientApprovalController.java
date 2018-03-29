package com.yunjing.approval.api;

import com.common.mybatis.page.Page;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.approval.util.DateUtil;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 客户端-审批服务接口
 *
 * @author 刘小鹏
 * @date 2018/03/22
 */
@RestController
@RequestMapping("/approval")
public class ClientApprovalController extends BaseController {

    @Autowired
    private IApprovalApiService approvalApiService;

    @Autowired
    private IModelItemService modelItemService;

    /**
     * 获取审批列表
     *
     * @param oid 企业主键
     * @return
     */
    @GetMapping("/index")
    public ResponseEntityWrapper index(@RequestParam("oid") Long oid) throws Exception {
        List<ClientModelVO> list = approvalApiService.getList(oid);
        return success(list);
    }

    /**
     * 获取审批模型详情
     *
     * @param modelId 模型主键
     * @return
     * @throws Exception
     */
    @GetMapping("/model-item-detail")
    public ResponseEntityWrapper getItem(@RequestParam("modelId") Long modelId) throws Exception {
        return success(modelItemService.getModelItem(modelId));
    }


    /**
     * 待我审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param oid       企业主键
     * @param uid       用户主键
     * @param searchKey 搜索标题
     * @return
     */
    @GetMapping("/waited-approval")
    public ResponseEntityWrapper waitedApproval(@ModelAttribute(value = "page") Page page,
                                                @RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                String searchKey) {

        return success(approvalApiService.getWaited(page, oid, uid, searchKey));
    }

    /**
     * 已审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param oid       企业主键
     * @param uid       用户主键
     * @param searchKey 搜索标题
     * @return
     */
    @GetMapping("/completed-approval")
    public ResponseEntityWrapper completedApproval(@ModelAttribute(value = "page") Page page,
                                                   @RequestParam("oid") Long oid,
                                                   @RequestParam("uid") Long uid,
                                                   String searchKey) {
        return success(approvalApiService.getCompleted(page, oid, uid, searchKey));
    }

    /**
     * 我发起的--审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param oid       企业主键
     * @param uid       用户主键
     * @param searchKey 搜索标题
     * @return
     */
    @GetMapping("/launched-approval")
    public ResponseEntityWrapper launchedApproval(@ModelAttribute(value = "page") Page page,
                                                  @RequestParam("oid") Long oid,
                                                  @RequestParam("uid") Long uid,
                                                  String searchKey) {

        return success(approvalApiService.getLaunched(page, oid, uid, searchKey));
    }

    /**
     * 抄送我的--审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param oid       企业主键
     * @param uid       用户主键
     * @param state     审批状态
     * @param searchKey 搜索标题
     * @return
     */
    @GetMapping("/copied-approval")
    public ResponseEntityWrapper copiedApproval(@ModelAttribute(value = "page") Page page,
                                                @RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                @RequestParam(value = "state", defaultValue = "0") Integer state, String searchKey) {

        return success(approvalApiService.getCopied(page, oid, uid, searchKey));
    }

    /**
     * 审批详情
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @return
     */
    @GetMapping("/approval-detail")
    public ResponseEntityWrapper approvalDetail(@RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                @RequestParam("approvalId") Long approvalId) {
        return success(approvalApiService.getApprovalDetail(oid, uid, approvalId));
    }
}
