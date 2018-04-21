package com.yunjing.approval.api;

import com.common.mybatis.page.Page;
import com.yunjing.approval.model.dto.OrgMemberMessage;
import com.yunjing.approval.model.vo.ClientModelVO;
import com.yunjing.approval.model.vo.Member;
import com.yunjing.approval.model.vo.MemberInfo;
import com.yunjing.approval.model.vo.OrgMemberVo;
import com.yunjing.approval.param.FilterParam;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.*;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
    @Autowired
    private IProcessService processService;

    /**
     * 获取审批列表
     *
     * @param companyId 公司id
     * @return
     */
    @GetMapping("/index")
    public ResponseEntityWrapper index(@RequestParam("companyId") String companyId) throws Exception {
        List<ClientModelVO> list = approvalApiService.getList(companyId);
        return success(list);
    }

    /**
     * 获取审批模型详情
     *
     * @param modelId 模型主键
     * @param memberId
     * @return
     * @throws Exception
     */
    @GetMapping("/model-item-detail")
    public ResponseEntityWrapper getItem(@RequestParam("modelId") String modelId,@RequestParam("memberId") String memberId) throws Exception {
        return success(modelItemService.getModelItem(modelId,memberId));
    }


    /**
     * 待我审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param companyId   公司id
     * @param memberId    成员id
     * @param filterParam 搜索参数
     * @return
     */
    @PostMapping("/waited-approval")
    public ResponseEntityWrapper waitedApproval(@ModelAttribute(value = "page") Page page,
                                                @RequestParam("companyId") String companyId,
                                                @RequestParam("memberId") String memberId,
                                                FilterParam filterParam) {

        return success(approvalApiService.getWaited(page, companyId, memberId, filterParam));
    }

    /**
     * 已审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param companyId   公司id
     * @param memberId    成员id
     * @param filterParam 搜索参数
     * @return
     */
    @PostMapping("/completed-approval")
    public ResponseEntityWrapper completedApproval(@ModelAttribute(value = "page") Page page,
                                                   @RequestParam("companyId") String companyId,
                                                   @RequestParam("memberId") String memberId,
                                                   FilterParam filterParam) {
        return success(approvalApiService.getCompleted(page, companyId, memberId, filterParam));
    }

    /**
     * 我发起的--审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param companyId   公司id
     * @param memberId    成员id
     * @param filterParam 搜索参数
     * @return
     */
    @PostMapping("/launched-approval")
    public ResponseEntityWrapper launchedApproval(@ModelAttribute(value = "page") Page page,
                                                  @RequestParam("companyId") String companyId,
                                                  @RequestParam("memberId") String memberId,
                                                  FilterParam filterParam) {

        return success(approvalApiService.getLaunched(page, companyId, memberId, filterParam));
    }

    /**
     * 抄送我的--审批列表
     *
     * @param page        @param page 分页对象  current 当前页码, size 页大小
     * @param companyId   公司id
     * @param memberId    成员id
     * @param filterParam 搜索参数
     * @return
     */
    @PostMapping("/copied-approval")
    public ResponseEntityWrapper copiedApproval(@ModelAttribute(value = "page") Page page,
                                                @RequestParam("companyId") String companyId,
                                                @RequestParam("memberId") String memberId,
                                                FilterParam filterParam) {

        return success(approvalApiService.getCopied(page, companyId, memberId, filterParam));
    }

    /**
     * 审批详情
     *
     * @param companyId  公司id
     * @param memberId   成员id
     * @param approvalId 审批主键
     * @return
     */
    @PostMapping("/approval-detail")
    public ResponseEntityWrapper approvalDetail(@RequestParam("companyId") String companyId,
                                                @RequestParam("memberId") String memberId,
                                                @RequestParam("approvalId") String approvalId) {
        return success(approvalApiService.getApprovalDetail(companyId, memberId, approvalId));
    }

    /**
     * 获取审批人
     *
     * @param modelId     模型主键
     * @param deptId      部门主键
     * @param conditionId 条件主键
     * @param judge       审批条件的值
     * @return
     */
    @PostMapping("/get-approver")
    public ResponseEntityWrapper getApprovalMember(@RequestParam("companyId") String companyId, @RequestParam("memberId") String memberId,
                                                   @RequestParam("modelId") String modelId,@RequestParam(value = "deptId",required = false) String deptId,
                                                   @RequestParam(value = "conditionId",required = false) String conditionId,
                                                   @RequestParam(value = "judge",required = false) String judge) throws Exception {
        return success(processService.getApprover(companyId, memberId, modelId, deptId, conditionId, judge));
    }

    @Autowired
    private AppCenterService appCenterService;

    @Autowired
    private IApprovalUserService approvalUserService;
    @GetMapping("/test")
    public ResponseEntityWrapper test(String companyId) {
        List<OrgMemberVo> allOrgMember = appCenterService.findAllOrgMember(companyId, true);
        List<String> memberIds = allOrgMember.stream().map(OrgMemberVo::getMemberId).collect(Collectors.toList());
        String[] ids = new String[memberIds.size()];
        String[] strings = memberIds.toArray(ids);
        List<Member> subLists = appCenterService.findSubLists(null, strings);
        List<OrgMemberMessage> list = new ArrayList<>();
        for (Member member : subLists) {
            OrgMemberMessage orgMemeber = new OrgMemberMessage();
            orgMemeber.setMemberId(member.getId());
            orgMemeber.setColor(member.getColor());
            orgMemeber.setCompanyId(member.getCompanyId());
            orgMemeber.setDeptIds(new ArrayList<>(member.getDeptIds()));
            orgMemeber.setDeptNames(member.getDeptNames());
            orgMemeber.setMemberName(member.getMemberName());
            orgMemeber.setMobile(member.getMobile());
            orgMemeber.setPosition(member.getPosition());
            orgMemeber.setProfile(member.getProfile());
            list.add(orgMemeber);
        }
        boolean b = approvalUserService.addMember(list);
        return success(b);
    }

}
