package com.yunjing.approval.processor.task.async;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.yunjing.approval.dao.mapper.ApprovalProcessMapper;
import com.yunjing.approval.dao.mapper.CopySMapper;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.CopyS;
import com.yunjing.approval.model.entity.PushLog;
import com.yunjing.approval.model.vo.ApprovalUserVO;
import com.yunjing.approval.model.vo.CopyUserVO;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.ICopySService;
import com.yunjing.approval.service.IPushLogService;
import com.yunjing.approval.util.DateUtil;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 审批推送任务
 *
 * @author 刘小鹏
 * @date 2018/03/28
 */
@Component
public class ApprovalPushTask extends BaseTask {


    private Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private IApprovalService approvalService;
    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;
    @Autowired
    private ICopySService copySService;
    @Autowired
    private CopySMapper copySMapper;
    @Autowired
    private IPushLogService pushLogService;

    /**
     * 审批主键
     */
    private Long approvalId;

    /**
     * 企业主键
     */
    private Long orgId;

    /**
     * 用户主键
     */
    private Long userId;

    /**
     * 任务名称
     */
    private final String TASK_NAME = "submitApproval";

    /**
     * 初始化任务参数
     *
     * @param approvalId 部门编号
     * @param orgId      部门编号
     * @param userId     部门编号
     * @return
     */
    public ApprovalPushTask init(Long approvalId, Long orgId, Long userId) {
        //设置线程名称
        super.setTaskName(TASK_NAME);
        //设置任务所需参数
        this.approvalId = approvalId;
        this.orgId = orgId;
        this.userId = userId;
        return this;
    }

    @Override
    public Object call() throws Exception {
        return null;
    }

    public void run() {
        try {
            logger.debug("开始审批推送任务,执行任务参数：approvalId = " + approvalId.toString() + "orgId = " + orgId + "userId = " + userId);
            submitApproval(approvalId, orgId, userId);
        } catch (Exception e) {
            logger.error("调用错误，错误原因：" + e.getMessage(), e);
        }
    }

    /**
     * 要推送的审批任务--实现方法
     *
     * @param approvalId 审批主键
     * @param orgId      企业主键
     * @param userId     用户主键
     */
    public void submitApproval(Long approvalId, Long orgId, Long userId) {
        Approval approval = approvalService.selectById(approvalId);
        // 发送推送
//        Push push = new Push();
        if (orgId != null && userId != null) {
            String message = "您收到一条审批消息";
            Map<String, Object> map = new HashMap<>(16);
            map.put("dataType", "20");
            map.put("id", approvalId);
            map.put("orgId", orgId);
            map.put("orgName", "");
            map.put("message", message);
            String systemMessage = JSONObject.toJSONString(map);
            logger.info("oid=" + orgId + "    msg=" + systemMessage);

            List<ApprovalUserVO> approvalUserList = approvalProcessMapper.getApprovalUserList(approvalId);
            Set<ApprovalUserVO> approvalProcessSet = new HashSet<>(approvalUserList);
            if (approvalProcessSet != null && !approvalProcessSet.isEmpty()) {
                String[] phones = new String[1];
                if (approval.getResult() == null) {
                    for (ApprovalUserVO approvalUserVO : approvalProcessSet) {
                        PushLog pushLog = new PushLog();
                        // 判断用户是否在平台登陆过
                        if (approvalUserVO.getIsActivated() == 1) {
                            if (approvalUserVO.getProcessState() == 0) {
                                phones[0] = approvalUserVO.getMobile();
                                pushLog.setDatatype(30);
                                pushLog.setInfoId(approvalId);
                                pushLog.setUserId(approvalUserVO.getUserId());
                                pushLog.setOrgId(orgId);
                                pushLog.setCopyNum(2);
                                pushLog.setCreateTime(DateUtil.getCurrentTime().getTime());
                                pushLog.setMessage("您收到一条审批消息");
                                boolean insert = pushLogService.insert(pushLog);
                                if (!insert) {
                                    throw new InsertMessageFailureException("保存推送审批记录失败");
                                }
//                               push.simplePush(1, user.getMobile(), phones, 1, message, systemMessage);
                                break;
                            }
                        }
                    }
                } else {

                    PushLog pushLog = new PushLog();
                    ApprovalUser approvalUser = approvalUserService.selectById(userId);
                    phones[0] = approvalUser.getMobile();
                    pushLog.setDatatype(30);
                    pushLog.setCopyNum(0);
                    pushLog.setInfoId(approval.getId());
                    pushLog.setUserId(approval.getUserId());
                    pushLog.setOrgId(orgId);
                    pushLog.setCreateTime(DateUtil.getCurrentTime().getTime());
                    pushLog.setMessage("您收到一条审批消息");
//                    push.simplePush(1, user.getUserMobile(), phones, 1, message, systemMessage);
                    boolean insert = pushLogService.insert(pushLog);
                    if (!insert) {
                        throw new InsertMessageFailureException("保存推送审批记录失败");
                    }
                    List<CopyS> approvCopys = copySService.selectList(Condition.create().where("approval_id={0}", approvalId));
                    List<CopyUserVO> copyUserList = copySMapper.getCopyUserList(approvalId);
                    Integer flag = 0;
                    if (approval.getResult() == 1) {
                        flag++;
                        System.out.println("========" + "第" + flag + "次进来");
                        int n = 1;
                        int i = 0;
                        List<String> userPhones = new ArrayList<String>();
                        List<String> phoes = new ArrayList<String>();
                        for (CopyUserVO copyVO : copyUserList) {
                            PushLog pushLog1 = new PushLog();
                            // 判断用户是否在平台登陆过
                            if (copyVO.getIsActivated() == 1) {
                                userPhones.add(copyVO.getMobile());
                                pushLog1.setDatatype(30);
                                pushLog1.setInfoId(approvalId);
                                pushLog1.setUserId(copyVO.getUserId());
                                pushLog1.setOrgId(orgId);
                                pushLog1.setCopyNum(1);
                                pushLog1.setCreateTime(DateUtil.getCurrentTime().getTime());
                                pushLog1.setMessage("您收到一条审批消息");
                                System.out.println("抄送人ID================" + copyVO.getUserId());
                                System.out.println("审批ID================" + approvalId);
                                pushLogService.insert(pushLog1);
                            }
                            if (n == 100) {
                                for (int j = 0; j < userPhones.size(); j++) {
                                    phones[j] = userPhones.get(j);
                                }
                                userPhones.removeAll(userPhones);
                                String[] photos = phoes.toArray(new String[phoes.size()]);
//                                push.simplePush(1, copyVO.getMobile(), photos, 1, message, systemMessage);
                                n = 1;
                            } else if (i == approvCopys.size() - 1 && n < 100) {
                                for (int j = 0; j < userPhones.size(); j++) {
                                    phoes.add(userPhones.get(j));
                                }
                                userPhones.removeAll(userPhones);
                                String[] photos = phoes.toArray(new String[phoes.size()]);
//                                push.simplePush(1, copyVO.getMobile(), photos, 1, message, systemMessage);
                            }
                            n++;
                            i++;
                        }
                    }
                }
            }
        }
    }
}
