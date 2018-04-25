package com.yunjing.approval.processor.task.async;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.yunjing.approval.dao.mapper.ApprovalProcessMapper;
import com.yunjing.approval.dao.mapper.CopysMapper;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.param.PushParam;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.Enum.DateStyle;
import com.yunjing.mommon.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
    /**
     * 绑定的前端审批域名
     */
    @Value("${botong.approval.h5domainName}")
    private String domainName;

    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private IApprovalService approvalService;
    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;
    @Autowired
    private CopysMapper copySMapper;
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
        ApprovalUser approvalUser = approvalUserService.selectById(memberId);
        // 推送审批消息发送人账号id
        String passportId = approvalUser.getPassportId();
        List<ApprovalUser> users = approvalUserService.selectList(Condition.create());
        if (companyId != null && memberId != null) {
            String message = "您收到一条审批消息";
            List<ApprovalUserVO> approvalUserList = approvalProcessMapper.getApprovalUserList(approvalId);
            List<ApprovalUserVO> approvalUserVOS = approvalUserList.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());
            Collections.sort(approvalUserVOS, new Comparator<ApprovalUserVO>() {
                @Override
                public int compare(ApprovalUserVO o1, ApprovalUserVO o2) {
                    if (o1.getSort() > o2.getSort()) {
                        return 1;
                    } else if (o1.getSort() < o2.getSort()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            if (approvalUserVOS != null && !approvalUserVOS.isEmpty()) {
                String[] passportIds = new String[1];
                if (approval.getResult() == null) {
                    for (ApprovalUserVO approvalUserVO : approvalUserVOS) {
                        if (approvalUserVO.getProcessState() == 0) {
                            List<ApprovalUser> collect = users.stream().filter(approvalUser1 -> approvalUser1.getId().equals(approvalUserVO.getUserId())).collect(Collectors.toList());
                            if(CollectionUtils.isNotEmpty(collect)){
                                passportIds[0] = collect.get(0).getPassportId();
                            }
                            // 审批推送入参
                            PushParam pushParam = setPushParam(passportId, passportIds, approval);
                            // 推送审批
                            appCenterService.push(pushParam);
                            break;
                        }
                    }
                } else {
                    List<ApprovalUser> collect = users.stream().filter(approvalUser1 -> approvalUser1.getId().equals(approval.getUserId())).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(collect)){
                        passportIds[0] = collect.get(0).getPassportId();
                    }
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
                        for (CopyUserVO copyVO : copyUserList) {
                            // 判断用户是否在平台登陆过
                            List<String> collect1 = users.stream().filter(approvalUser1 -> approvalUser1.getId().equals(copyVO.getUserId())).map(ApprovalUser::getPassportId).collect(Collectors.toList());
                            passIds.addAll(collect1);
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

    private PushParam setPushParam(String passportId, String[] passportIds, Approval approval) {
        ClientApprovalDetailVO approvalDetail = approvalApiService.getApprovalDetail(companyId, memberId, approval.getId());
        List<ApproveAttrVO> approveAttrVO = approvalDetail.getApproveAttrVO();
        ModelL modelL = modelService.selectById(approval.getModelId());
        String message = "您收到一条审批消息";
        logger.info("passportId: " + passportId + "  passportIds: " + passportIds[0] + "  message: " + message);
        PushParam pushParam = new PushParam();
        pushParam.setAppId(appId);
        pushParam.setMsg(message);
        pushParam.setAlias(passportIds);
        pushParam.setNotificationTitle(message);
        pushParam.setCompanyId(companyId);
        pushParam.setRegistrationId(passportId);
        pushParam.setTitle(message);
        Map<String, String> maps = new HashMap<>(5);
        maps.put("appName", "审批");
        maps.put("subModuleName", modelL.getModelName());
        maps.put("url", domainName+"approvalId=" + approval.getId());
        // 审批提醒
        JSONArray array = new JSONArray();
        JSONObject json1 = new JSONObject();
        json1.put("subTitle", approval.getTitle() + "需要您审批");
        json1.put("type", "5");
        array.add(json1);
        if (CollectionUtils.isNotEmpty(approveAttrVO)) {
            for (ApproveAttrVO vo : approveAttrVO) {
                JSONObject json2 = new JSONObject();
                if (vo.getType() == ApproConstants.RADIO_TYPE_3 || vo.getType() == ApproConstants.TIME_INTERVAL_TYPE_5 || vo.getType() == ApproConstants.SINGLE_LINE_TYPE_6) {
                    json2.put("title", vo.getLabel());
                    if ("开始时间".equals(vo.getLabel())) {
                        json2.put("content", DateUtil.StringToString(DateUtil.convert(Long.valueOf(vo.getValue())), DateStyle.YYYY_MM_DD_HH_MM));
                    } else {
                        json2.put("content", vo.getValue());
                    }
                    json2.put("type", "0");
                    array.add(json2);
                    if (StringUtils.isNotBlank(vo.getLabels())) {
                        JSONObject json5 = new JSONObject();
                        json5.put("title", vo.getLabels());
                        json5.put("content", DateUtil.StringToString(DateUtil.convert(Long.valueOf(vo.getValues())), DateStyle.YYYY_MM_DD_HH_MM));
                        json5.put("type", "0");
                        array.add(json5);
                    }
                }

            }
        }
        JSONObject json3 = new JSONObject();
        json3.put("type", "3");
        if (approval.getResult() != null) {
            switch (approval.getResult()) {
                case 1:
                    json3.put("status", "已同意");
                    json3.put("color", "#4FA97B");
                    break;
                case 2:
                    json3.put("status", "已拒绝");
                    json3.put("color", "#EA6262");
                    break;
            }
        } else {
            json3.put("status", "待审批");
            json3.put("color", "#848484");
        }
        array.add(json3);
        JSONObject json4 = new JSONObject();
        json4.put("bottom", approvalDetail.getName() + "  " + DateUtil.StringToString(DateUtil.convert(approval.getCreateTime()), DateStyle.YYYY_MM_DD_HH_MM));
        json4.put("type", "4");
        array.add(json4);
        maps.put("content", array.toJSONString());
        pushParam.setMap(maps);
        logger.info("pushParam: " + pushParam.toString());
        return pushParam;

    }
}
