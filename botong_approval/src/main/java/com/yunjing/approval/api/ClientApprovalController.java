package com.yunjing.approval.api;

import com.common.mybatis.page.Page;
import com.yunjing.approval.model.vo.ClientModelVO;
import com.yunjing.approval.param.FilterParam;
import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param oid         企业主键
     * @param uid         用户主键
     * @param filterParam 搜索参数
     * @return
     */
    @PostMapping("/waited-approval")
    public ResponseEntityWrapper waitedApproval(@ModelAttribute(value = "page") Page page,
                                                @RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                FilterParam filterParam) {

        return success(approvalApiService.getWaited(page, oid, uid, filterParam));
    }

    /**
     * 已审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param oid         企业主键
     * @param uid         用户主键
     * @param filterParam 搜索参数
     * @return
     */
    @PostMapping("/completed-approval")
    public ResponseEntityWrapper completedApproval(@ModelAttribute(value = "page") Page page,
                                                   @RequestParam("oid") Long oid,
                                                   @RequestParam("uid") Long uid,
                                                   FilterParam filterParam) {
        return success(approvalApiService.getCompleted(page, oid, uid, filterParam));
    }

    /**
     * 我发起的--审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param oid         企业主键
     * @param uid         用户主键
     * @param filterParam 搜索参数
     * @return
     */
    @PostMapping("/launched-approval")
    public ResponseEntityWrapper launchedApproval(@ModelAttribute(value = "page") Page page,
                                                  @RequestParam("oid") Long oid,
                                                  @RequestParam("uid") Long uid,
                                                  FilterParam filterParam) {

        return success(approvalApiService.getLaunched(page, oid, uid, filterParam));
    }

    /**
     * 抄送我的--审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param oid         企业主键
     * @param uid         用户主键
     * @param filterParam 搜索参数
     * @return
     */
    @PostMapping("/copied-approval")
    public ResponseEntityWrapper copiedApproval(@ModelAttribute(value = "page") Page page,
                                                @RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                FilterParam filterParam) {

        return success(approvalApiService.getCopied(page, oid, uid, filterParam));
    }

    /**
     * 审批详情
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @return
     */
    @PostMapping("/approval-detail")
    public ResponseEntityWrapper approvalDetail(@RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                @RequestParam("approvalId") Long approvalId) {
        return success(approvalApiService.getApprovalDetail(oid, uid, approvalId));
    }
}
