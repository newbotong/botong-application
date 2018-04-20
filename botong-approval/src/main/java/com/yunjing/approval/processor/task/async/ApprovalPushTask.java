package com.yunjing.approval.processor.task.async;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunjing.approval.dao.mapper.ApprovalProcessMapper;
import com.yunjing.approval.dao.mapper.CopysMapper;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.entity.PushLog;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.param.PushParam;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 审批推送任务
 *
 * @author 刘小鹏
 * @date 2018/03/28
 */
@Component
public class ApprovalPushTask extends BaseTask {


    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * 绑定的审批appId
     */
    @Value("${botong.approval.appId}")
    private String appId;
    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private IApprovalService approvalService;
    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;
    @Autowired
    private CopysMapper copySMapper;
    @Autowired
    private IPushLogService pushLogService;
    @Autowired
    private AppCenterService appCenterService;
    @Autowired
    private IApprovalApiService approvalApiService;
    @Autowired
    private IModelService modelService;

    /**
     * 审批主键
     */
    private String approvalId;

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 成员id
     */
    private String memberId;

    /**
     * 任务名称
     */
    private final String TASK_NAME = "submitApproval";

    /**
     * 初始化任务参数
     *
     * @param approvalId 审批id
     * @param companyId  公司id
     * @param memberId   成员id
     * @return
     */
    public ApprovalPushTask init(String approvalId, String companyId, String memberId) {
        //设置线程名称
        super.setTaskName(TASK_NAME);
        //设置任务所需参数
        this.approvalId = approvalId;
        this.companyId = companyId;
        this.memberId = memberId;
        return this;
    }

    @Override
    public Object call() throws Exception {
        return null;
    }

    public void run() {
        try {
            logger.debug("开始审批推送任务,执行任务参数：approvalId = " + approvalId.toString() + "orgId = " + companyId + "memberId = " + memberId);
            submitApproval(approvalId, companyId, memberId);
        } catch (Exception e) {
            logger.error("调用错误，错误原因：" + e.getMessage(), e);
        }
    }

    /**
     * 要推送的审批任务--实现方法
     *
     * @param approvalId 审批主键
     * @param companyId  公司id
     * @param memberId   成员id
     */
    public void submitApproval(String approvalId, String companyId, String memberId) {
        logger.info("approval: " + approvalId + "  companyId: " + companyId + "  memberId: " + memberId);
        Approval approval = approvalService.selectById(approvalId);
        ApprovalUser user = approvalUserService.selectById(companyId);
        String[] members = new String[1];
        members[0] = memberId;
        List<Member> memberList = appCenterService.findSubLists(null, members);
        String passportId = "";
        for (Member member : memberList) {
            passportId = member.getPassportId();
        }
        if (companyId != null && memberId != null) {
            String message = "您收到一条审批消息";
            List<ApprovalUserVO> approvalUserList = approvalProcessMapper.getApprovalUserList(approvalId);
            Set<ApprovalUserVO> approvalProcessSet = new HashSet<>(approvalUserList);
            if (approvalProcessSet != null && !approvalProcessSet.isEmpty()) {
                String[] passportIds = new String[1];
                String[] mid = new String[1];
                if (approval.getResult() == null) {
                    for (ApprovalUserVO approvalUserVO : approvalProcessSet) {
                        if (approvalUserVO.getProcessState() == 0) {
                            mid[0] = approvalUserVO.getUserId();
                            List<Member> mlist = appCenterService.findSubLists(null, mid);
                            for (Member member : mlist) {
                                passportIds[0] = member.getPassportId();
                            }
                            // 审批推送入参
                            PushParam pushParam = setPushParam(passportId, passportIds, approval);
                            // 推送审批
                            appCenterService.push(pushParam);
                            break;
                        }
                    }
                } else {

                    passportIds[0] = user.getId();
                    // 审批推送入参
                    PushParam pushParam = setPushParam(passportId, passportIds, approval);
                    // 推送审批
                    appCenterService.push(pushParam);
                    List<CopyUserVO> copyUserList = copySMapper.getCopyUserList(approvalId);
                    Integer flag = 0;
                    if (approval.getResult() == 1) {
                        flag++;
                        logger.info("========" + " 第" + flag + "次进来");
                        int n = 1;
                        int i = 0;
                        List<String> passIds = new ArrayList<>();
                        List<String> passIdList = new ArrayList<>();
                        String[] cid = new String[1];
                        for (CopyUserVO copyVO : copyUserList) {
                            // 判断用户是否在平台登陆过
                            cid[0] = copyVO.getUserId();
                            List<Member> mids = appCenterService.findSubLists(null, cid);
                            List<String> pids = mids.stream().map(Member::getPassportId).collect(Collectors.toList());
                            passIds.addAll(pids);
                            if (n == 100) {
                                for (int j = 0; j < passIds.size(); j++) {
                                    passportIds[j] = passIds.get(j);
                                }
                                passIds.removeAll(passIds);
                                String[] pidArray = passIdList.toArray(new String[passIdList.size()]);
                                // 审批推送入参
                                PushParam pushParam2 = setPushParam(passportId, pidArray, approval);
                                // 推送审批
                                appCenterService.push(pushParam2);
                                n = 1;
                            } else if (i == copyUserList.size() - 1 && n < 100) {
                                for (int j = 0; j < passIds.size(); j++) {
                                    passIdList.add(passIds.get(j));
                                }
                                passIds.removeAll(passIds);
                                String[] pidArray = passIdList.toArray(new String[passIdList.size()]);
                                // 审批推送入参
                                PushParam pushParam2 = new PushParam();
                                pushParam2.setMsg(message);
                                pushParam2.setAlias(pidArray);
                                pushParam2.setRegistrationId(passportId);
                                pushParam2.setNotificationTitle(message);
                                // 推送审批
                                appCenterService.push(pushParam2);
                            }
                            n++;
                            i++;
                        }
                    }
                }
            }
        }
    }

    private PushParam setPushParam(String passportId, String[] passportIds,Approval approval) {
        ClientApprovalDetailVO approvalDetail = approvalApiService.getApprovalDetail(companyId, memberId, approval.getId());
        List<ApproveAttrVO> approveAttrVO = approvalDetail.getApproveAttrVO();
        ModelL modelL = modelService.selectById(approval.getModelId());
        String message = "您收到一条审批消息";
        logger.info("passportId: " + passportId + "  passportIds: " + passportIds[0] + "  message: " + message);

        PushParam pushParam = new PushParam();
        pushParam.setAppId(appId);
        pushParam.setCompanyId(companyId);
        pushParam.setMsg(message);
        pushParam.setAlias(passportIds);
        pushParam.setNotificationTitle(message);
        pushParam.setRegistrationId(passportId);
        pushParam.setTitle(message);

        Map<String, String> maps = new HashMap<>(5);
        maps.put("subModuleName", modelL.getModelName());
        maps.put("url", "http://www.shenpi.com");

        // 审批提醒
        JSONArray array = new JSONArray();
        JSONObject json;

        json = new JSONObject();
        json.put("subTitle", approval.getTitle()+" 需要您审批");
        json.put("type", "5");

        if(CollectionUtils.isNotEmpty(approveAttrVO)){
            for (ApproveAttrVO vo : approveAttrVO) {
                if(vo.getType()== ApproConstants.RADIO_TYPE_3|| vo.getType() == ApproConstants.TIME_INTERVAL_TYPE_5){
                    json = new JSONObject();
                    json.put("title", vo.getLabel());
                    json.put("content", vo.getValue());
                    json.put("type", "0");
                    array.add(json);
                }
            }
            json = new JSONObject();
            json.put("bottom", approvalDetail.getName() + "  " + DateUtil.convert(approval.getCreateTime()));
            json.put("type", "4");
            array.add(json);

            json = new JSONObject();
            switch (approval.getResult()){
                case 1:
                    json.put("status", "已同意");
                    json.put("color", "#4FA97B");
                    break;
                case 2:
                    json.put("status", "已拒绝");
                    json.put("color", "#EA6262");
                    break;
                case 3:
                    json.put("status", "已转交");
                    json.put("color", "#EA6262");
                    break;
                case 4:
                    json.put("status", "已撤销");
                    json.put("color", "#848484");
                    break;
                default:
                    json.put("status", "待审批");
                    json.put("color", "#848484");

            }
            json.put("type", "3");
            array.add(json);
        }
        maps.put("content", array.toJSONString());
        pushParam.setMap(maps);
        logger.info("pushParam: " + pushParam.toString());
        return pushParam;

    }
}

