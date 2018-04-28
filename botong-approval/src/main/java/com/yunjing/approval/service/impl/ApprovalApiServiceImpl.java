package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.common.mybatis.page.Page;
import com.yunjing.approval.dao.mapper.*;
import com.yunjing.approval.model.dto.ApprovalContentDTO;
import com.yunjing.approval.model.dto.ApprovalDetailDTO;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.param.FilterParam;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.processor.task.async.ApprovalPushTask;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.Enum.DateStyle;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuxiaopeng
 * @date 2018/02/28
 */
@Service("approvalApiService")
public class ApprovalApiServiceImpl implements IApprovalApiService {

    @Autowired
    private IModelService modelService;
    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;
    @Autowired
    private IApprovalProcessService approvalProcessService;
    @Autowired
    private CopysMapper copysMapper;
    @Autowired
    private ICopysService copySService;
    @Autowired
    private ApprovalMapper approvalMapper;
    @Autowired
    private IApprovalService approvalService;
    @Autowired
    private ApprovalPushTask approvalPushTask;
    @Autowired
    private ApprovalAttrMapper approvalAttrMapper;
    @Autowired
    private AppCenterService appCenterService;

    private final Log logger = LogFactory.getLog(ApprovalApiServiceImpl.class);

    @Override
    public List<ClientModelVO> getList(String companyId) {
        logger.info("companyId: " + companyId);
        List<ModelL> modelLList = modelMapper.selectModelListByOrgId(companyId);
        List<ClientModelVO> list = new ArrayList<>();
        for (ModelL model : modelLList) {
            ClientModelVO modelVO1 = new ClientModelVO(model);
            list.add(modelVO1);
        }
        return list;
    }

    @Override
    public Page<ClientApprovalVO> getWaited(Page page, String companyId, String memberId, FilterParam filterParam) {
        logger.info("companyId: " + companyId + " memberId: " + memberId + " filterParam: " + filterParam.toString());
        // 全部
        int all = 9;
        if (filterParam.getState() != null && filterParam.getState() == all) {
            filterParam.setState(null);
        }
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        String deptId = filterParam.getDeptId();
        // 查询部门中成员
        String[] did = new String[]{deptId};
        List<Member> members = appCenterService.findSubLists(did, null);
        List<String> userIds = new ArrayList<>();
        if (members != null) {
            userIds = members.stream().map(Member::getId).collect(Collectors.toList());
        }
        userIds.add(memberId);
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> waitedMeApprovalList = approvalProcessMapper.getWaitedMeApprovalList(index, size, companyId, userIds, filterParam);
        List<ApprovalContentDTO> result = new ArrayList<>();
        for (ApprovalContentDTO contentDTO : waitedMeApprovalList) {
            List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().where("approval_id={0}", contentDTO.getApprovalId()).and("user_id={0}", memberId));
            for (ApprovalProcess process : processList) {
                if (process.getSeq() == 1) {
                    result.add(contentDTO);
                } else {
                    List<ApprovalProcess> processList1 = approvalProcessService.selectList(Condition.create().where("approval_id={0}", contentDTO.getApprovalId()));
                    for (ApprovalProcess aProcess : processList1) {
                        //判断上一个审批人的审批状态，如果是1（同意）或者是3（转让）显示当前审批人
                        if (aProcess.getSeq() + 1 == process.getSeq() && aProcess.getProcessState() == 1 || aProcess.getProcessState() == 3) {
                            result.add(contentDTO);
                        }
                    }
                }
            }
        }
        convertList(clientApprovalVOS, result);
        clientApprovalVOPage.build(clientApprovalVOS != null ? clientApprovalVOS : new ArrayList<>());
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getCompleted(Page page, String companyId, String memberId, FilterParam filterParam) {
        logger.info("companyId: " + companyId + " memberId: " + memberId + " filterParam: " + filterParam.toString());
        // 全部
        int all = 9;
        if (filterParam.getState() != null && filterParam.getState() == all) {
            filterParam.setState(null);
        }
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        String deptId = filterParam.getDeptId();
        // 查询部门中成员
        String[] did = new String[]{deptId};
        List<Member> members = appCenterService.findSubLists(did, null);
        List<String> userIds = new ArrayList<>();
        if (members != null) {
            userIds = members.stream().map(Member::getId).collect(Collectors.toList());
        }
        userIds.add(memberId);
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> completedMeApprovalList = approvalProcessMapper.getCompletedApprovalList(index, size, companyId, userIds, filterParam);
        convertList(clientApprovalVOS, completedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS != null ? clientApprovalVOS : new ArrayList<>());
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getLaunched(Page page, String companyId, String memberId, FilterParam filterParam) {
        // 全部
        int all = 9;
        if (filterParam.getState() != null && filterParam.getState() == all) {
            filterParam.setState(null);
        }
        if (filterParam.getTime() != null) {
            String date = DateUtil.convert(filterParam.getTime()).replace("00:00:00", "08:00:00");
            filterParam.setTime(DateUtil.StringToDate(date, DateStyle.YYYY_MM_DD_HH_MM_SS).getTime());
        }
        logger.info("companyId: " + companyId + " memberId: " + memberId + " filterParam: " + filterParam.toString());
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> launchedMeApprovalList = approvalProcessMapper.getLaunchedApprovalList(index, size, companyId, memberId, filterParam);
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());
        String message = "";
        ApprovalUser user = new ApprovalUser();
        for (ApprovalContentDTO contentVO : launchedMeApprovalList) {
            if (contentVO.getState() == 0) {
                List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().where("approval_id={0}", contentVO.getApprovalId()).orderBy("seq", true));
                if (processList != null && CollectionUtils.isNotEmpty(processList)) {
                    for (ApprovalProcess process : processList) {
                        if (process.getProcessState() == 0) {
                            List<ApprovalUser> users = userList.stream().filter(approvalUser -> approvalUser.getId().equals(process.getUserId())).collect(Collectors.toList());
                            for (ApprovalUser approvalUser : users) {
                                user = approvalUser;
                            }
                            String name = StringUtils.isNotBlank(user.getName()) ? user.getName() : "";
                            if (name.length() > 2) {
                                message = "等待" + name.substring(1, 3) + "审批";
                            } else {
                                message = "等待" + name + "审批";
                            }
                            contentVO.setMessage(message);
                            break;
                        }
                    }
                }
            } else if (contentVO.getState() == 1) {
                message = "审批完成";
                if (contentVO.getResult() != null && contentVO.getResult() == 1) {
                    message += " （同意）";
                } else {
                    message += " （拒绝）";
                }
            } else if (contentVO.getState() == 2) {
                message = "已撤销";
            }
            contentVO.setMessage(message);
        }
        convertList(clientApprovalVOS, launchedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS != null ? clientApprovalVOS : new ArrayList<>());
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getCopied(Page page, String companyId, String memberId, FilterParam filterParam) {
        logger.info("companyId: " + companyId + " memberId: " + memberId + " filterParam: " + filterParam.toString());
        // 全部
        int all = 9;
        if (filterParam.getState() != null && filterParam.getState() == all) {
            filterParam.setState(null);
        }
        if (filterParam.getTime() != null) {
            String date = DateUtil.convert(filterParam.getTime()).replace("00:00:00", "08:00:00");
            filterParam.setTime(DateUtil.StringToDate(date, DateStyle.YYYY_MM_DD_HH_MM_SS).getTime());
        }
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> copyApprovalList = copysMapper.getCopiedApprovalList(index, size, companyId, memberId, filterParam);
        convertList(clientApprovalVOS, copyApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS != null ? clientApprovalVOS : new ArrayList<>());
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public ClientApprovalDetailVO getApprovalDetail(String companyId, String memberId, String approvalId) {
        logger.info("companyId: " + companyId + " memberId: " + memberId + " approvalId: " + approvalId);
        ClientApprovalDetailVO clientApprovalDetailVO = new ClientApprovalDetailVO();
        // 获取审批详情
        List<ApproveAttrVO> detail = getDetail(approvalId);
        // 注入审批信息详情
        clientApprovalDetailVO.setApproveAttrVO(detail);

        // 根据审批主键查询审批信息
        ApprovalDetailDTO approvalById = approvalMapper.getApprovalById(approvalId);
        if (approvalById != null) {
            // 审批主体信息
            clientApprovalDetailVO.setName(approvalById.getName());
            clientApprovalDetailVO.setMemberId(approvalById.getUserId());
            clientApprovalDetailVO.setPassportId(approvalById.getPassportId());
            ModelL modelL = modelService.selectOne(Condition.create().where("id={0}", approvalById.getModelId()));
            clientApprovalDetailVO.setModelName(modelL.getModelName() != null ? modelL.getModelName() : null);
            String[] deptIds = approvalById.getDeptId().split(",");
            String[] deptNames = approvalById.getDeptName().split(",");
            String deptName = "部门名称";
            for (int i = 0; i < (deptIds.length < deptNames.length ? deptNames.length : deptIds.length); i++) {
                if (approvalById.getDeptPartId().equals(deptIds[i])) {
                    deptName = deptNames[i];
                }
            }
            clientApprovalDetailVO.setDeptName(deptName);
            clientApprovalDetailVO.setPosition(approvalById.getPosition());
            if (StringUtils.isNotBlank(approvalById.getAvatar())) {
                clientApprovalDetailVO.setAvatar(approvalById.getAvatar());
            } else {
                clientApprovalDetailVO.setColor(ApproConstants.DEFAULT_COLOR);
            }
            clientApprovalDetailVO.setState(approvalById.getState());
            clientApprovalDetailVO.setResult(approvalById.getResult() != null ? approvalById.getResult() : null);
        }
        // 获取审批人信息
        List<ApprovalUserVO> approvalUserList = approvalProcessMapper.getApprovalUserList(approvalId);
        // 审批发起人
        ApprovalUserVO initiator = new ApprovalUserVO();
        if (approvalById != null) {
            initiator.setName(approvalById.getName() != null ? approvalById.getName() : "");
            initiator.setAvatar(approvalById.getAvatar() != null ? approvalById.getAvatar() : "");
            initiator.setApprovalTime(approvalById.getCreateTime());
            initiator.setColor(approvalById.getColor() != null ? approvalById.getColor() : ApproConstants.DEFAULT_COLOR);
        }
        initiator.setMessage("发起申请");
        initiator.setProcessState(10);
        initiator.setSort(0);
        approvalUserList.add(initiator);
        int index = 1;
        for (ApprovalUserVO approvalUserVO : approvalUserList) {
            approvalUserVO.setColor(approvalUserVO.getColor() != null ? approvalUserVO.getColor() : ApproConstants.DEFAULT_COLOR);
            if (StringUtils.isBlank(approvalUserVO.getAvatar())) {
                approvalUserVO.setColor(approvalUserVO.getColor() != null ? approvalUserVO.getColor() : ApproConstants.DEFAULT_COLOR);
            }
            if (approvalUserVO.getProcessState() != null && approvalUserVO.getProcessState() == 0) {
                approvalUserVO.setApprovalTime(null);
                int i = index++;
                if (approvalUserVO.getUserId().equals(memberId)) {
                    //描述提醒用户信息
                    clientApprovalDetailVO.setProcessState(approvalUserVO.getProcessState());
                    clientApprovalDetailVO.setMessage("等待我审批");
                    if (i == 1) {
                        approvalUserVO.setMessage("审批中");
                    } else {
                        approvalUserVO.setMessage("等待审批");
                    }
                } else {
                    //描述提醒用户信息
                    if (i == 1) {
                        clientApprovalDetailVO.setMessage("等待" + approvalUserVO.getName() + "审批");
                        approvalUserVO.setMessage("审批中");
                    } else {
                        approvalUserVO.setMessage("等待审批");
                    }
                }
            } else if (approvalUserVO.getProcessState() != null && approvalUserVO.getProcessState() == 1) {
                approvalUserVO.setMessage("已同意");
            } else if (approvalUserVO.getProcessState() != null && approvalUserVO.getProcessState() == 2) {
                approvalUserVO.setMessage("已拒绝");
            } else if (approvalUserVO.getProcessState() != null && approvalUserVO.getProcessState() == 3) {
                approvalUserVO.setMessage("已转交");
            } else if (approvalUserVO.getProcessState() != null && approvalUserVO.getProcessState() == 4) {
                approvalUserVO.setMessage("已撤销");
            }
        }
        Collections.sort(approvalUserList, new Comparator<ApprovalUserVO>() {
            @Override
            public int compare(ApprovalUserVO o1, ApprovalUserVO o2) {
                if (o1.getSort() > o2.getSort()) {
                    return 1;
                } else if (o1.getSort() < (o2.getSort())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        // 当发起人做撤销操作后审批详情中审批人情况集合
        Set<ApprovalUserVO> collect = approvalUserList.stream().filter(approvalUserVO -> approvalUserVO.getProcessState() == 4).collect(Collectors.toSet());
        if (collect.size() > 0) {
            boolean b = approvalUserList.removeIf(approvalUserVO -> approvalUserVO.getProcessState() == 4);
            if (b) {
                ApprovalUserVO initiator2 = new ApprovalUserVO();
                initiator2.setName(null != approvalById.getName() ? approvalById.getName() : "");
                initiator2.setAvatar(approvalById.getAvatar() != null ? approvalById.getAvatar() : "");
                initiator2.setApprovalTime(approvalById.getFinishTime() != null ? approvalById.getFinishTime() : null);
                initiator2.setColor(approvalById.getColor() != null ? approvalById.getColor() : ApproConstants.DEFAULT_COLOR);
                initiator2.setMessage("已撤销");
                initiator2.setProcessState(4);
                initiator2.setSort(1);
                approvalUserList.add(initiator2);
                clientApprovalDetailVO.setApprovalUserList(approvalUserList);
            }
        } else {
            clientApprovalDetailVO.setApprovalUserList(approvalUserList);
        }
        // 当有审批人做拒绝操作后审批详情中移除正在审批的审批人集合
        Set<ApprovalUserVO> collect1 = approvalUserList.stream().filter(approvalUserVO -> approvalUserVO.getProcessState() == 2).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(collect1)) {
            approvalUserList.removeIf(approvalUserVO -> approvalUserVO.getProcessState() == 0);
        }
        // 获取抄送人信息
        List<CopyUserVO> copyUserList = copysMapper.getCopyUserList(approvalId);
        copyUserList.forEach(copyUserVO -> {
            if (StringUtils.isBlank(copyUserVO.getAvatar())) {
                copyUserVO.setColor(copyUserVO.getColor() != null ? copyUserVO.getColor() : ApproConstants.DEFAULT_COLOR);
            }
        });
        // 注入抄送人信息
        clientApprovalDetailVO.setCopyUserList(copyUserList);
        return clientApprovalDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean solveApproval(String companyId, String memberId, String approvalId, Integer state) {
        logger.info("companyId: " + companyId + " memberId: " + memberId + " approvalId: " + approvalId + "state: " + state);
        boolean flag = false;
        List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().where("approval_id={0}", approvalId).orderBy("seq", true));
        if (processList != null && CollectionUtils.isNotEmpty(processList)) {
            for (ApprovalProcess process : processList) {
                // 当审批流程中的审批未处理时（0：未处理）
                if (process.getProcessState() == 0) {
                    if (process.getUserId().equals(memberId)) {
                        process.setProcessState(state);
                        process.setProcessTime(System.currentTimeMillis());
                        boolean update = approvalProcessService.update(process, Condition.create().where("approval_id={0}", approvalId).and("user_id={0}", memberId));
                        if (!update) {
                            throw new UpdateMessageFailureException("同意审批--更新审批流程信息失败");
                        }
                    }
                    break;
                }
            }
            Approval approval = approvalService.selectById(approvalId);
            if (approval != null) {
                // 2表示审批流程中的拒绝 （0:未处理 1:同意 2:拒绝 3:转交）
                int revoke = 2;
                if (state != revoke) {
                    int num = processList.get(processList.size() - 1).getProcessState();
                    switch (num) {
                        case 1:
                            // 状态 1:审批完成
                            approval.setState(1);
                            // 结果 1:已同意
                            approval.setResult(1);
                            break;
                        case 2:
                            // 状态 1:审批完成
                            approval.setState(1);
                            // 结果 2:已拒绝
                            approval.setResult(2);
                            break;
                        case 4:
                            // 2:已撤回
                            approval.setState(2);
                            // 4:已撤销
                            approval.setResult(4);
                            break;
                        default:
                            // 2:已撤回
                            approval.setState(approval.getState());
                            // 4:已撤销
                            approval.setResult(approval.getResult());
                    }
                } else if (state == revoke) {
                    // 状态 1:审批完成
                    approval.setState(1);
                    // 结果 2:已拒绝
                    approval.setResult(2);
                }
                if (approval.getResult() != null) {
                    // 保存审批完成时间
                    approval.setFinishTime(System.currentTimeMillis());
                    List<Copys> copysList = copySService.selectList(Condition.create().where("approval_id={0}", approvalId));
                    // 更新抄送信息
                    if (approval.getResult() == 1 && !copysList.isEmpty()) {
                        for (Copys copys : copysList) {
                            copys.setCopySType(1);
                            copys.setCreateTime(System.currentTimeMillis());
                        }
                        boolean batchById = copySService.updateBatchById(copysList);
                        if (!batchById) {
                            throw new UpdateMessageFailureException("同意审批操作中--更新抄送信息失败");
                        }
                    }
                }

            }
            flag = approvalService.updateById(approval);
            if (!flag) {
                throw new UpdateMessageFailureException("同意审批操作中--更新审批信息失败");
            }
        }
        //异步推送给下一个审批人
        if (flag) {
            approvalPushTask.init(approvalId, companyId, memberId).run();
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeApproval(String companyId, String memberId, String approvalId) {
        logger.info("companyId: " + companyId + " memberId: " + memberId + " approvalId: " + approvalId);
        boolean flag = false;
        List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().where("approval_id={0}", approvalId));
        processList.forEach(approvalProcess -> {
            approvalProcess.setProcessState(4);
            approvalProcess.setProcessTime(System.currentTimeMillis());
        });
        boolean batchById = approvalProcessService.updateBatchById(processList);
        if (!batchById) {
            throw new UpdateMessageFailureException("审批撤销操作--批量修改审批流程信息失败");
        }
        Approval approval = approvalService.selectById(approvalId);
        if (approval != null) {
            approval.setState(2);
            approval.setResult(4);
            approval.setFinishTime(System.currentTimeMillis());
            flag = approvalService.updateById(approval);
            if (!flag) {
                throw new UpdateMessageFailureException("审批撤销操作--修改审批信息失败");
            }
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferApproval(String companyId, String memberId, String transferredUserId, String approvalId) {
        logger.info("companyId: " + companyId + " memberId: " + memberId + " transferredUserId: " + transferredUserId + "approvalId: " + approvalId);
        Approval approval = approvalService.selectById(approvalId);
        if (approval != null && approval.getUserId().equals(transferredUserId)) {
            throw new ParameterErrorException("不能将审批转交给审批发起人");
        }
        List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().where("approval_id={0}", approvalId).orderBy("seq", true));
        int num = 0;
        List<ApprovalProcess> list = new ArrayList<>();
        for (ApprovalProcess approvalProcess : processList) {
            if (approvalProcess.getProcessState() == 0 && num == 0) {
                approvalProcess.setProcessState(3);
                approvalProcess.setProcessTime(System.currentTimeMillis());
                list.add(approvalProcess);
                ApprovalProcess newProcess = new ApprovalProcess();
                newProcess.setSeq(approvalProcess.getSeq() + 1);
                newProcess.setId(IDUtils.uuid());
                newProcess.setUserId(transferredUserId);
                newProcess.setProcessState(0);
                newProcess.setApprovalId(approvalId);
                newProcess.setProcessTime(System.currentTimeMillis());
                boolean insert = approvalProcessService.insert(newProcess);
                if (!insert) {
                    throw new InsertMessageFailureException("转让审批操作--新增审批流程信息失败");
                }
                num++;
                continue;
            }
            if (num == 1) {
                approvalProcess.setSeq(approvalProcess.getSeq() + 1);
                list.add(approvalProcess);
            } else {
                list.add(approvalProcess);
            }
        }
        boolean batchById = approvalProcessService.updateBatchById(list);
        if (!batchById) {
            throw new UpdateMessageFailureException("转让审批操作--修改审批流程信息失败");
        }
        //转让成功推送下一个
        if (batchById) {
            approvalPushTask.init(approvalId, companyId, memberId).run();
        }
        return batchById;
    }

    @Override
    public boolean updateCopyReadState(String[] approvalId) {
        logger.info("approvalId: " + approvalId.toString());
        boolean isUpdated = false;

        List<Copys> copysList = copySService.selectList(Condition.create().in("approval_id", approvalId));
        copysList.forEach(copys -> {
            copys.setIsRead(1);
        });
        if (!copysList.isEmpty()) {
            isUpdated = copySService.updateBatchById(copysList);
        }
        return isUpdated;
    }

    private void convertList(List<ClientApprovalVO> clientApprovalVOS, List<ApprovalContentDTO> approvalList) {
        // 获取所有用户
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());
        for (ApprovalContentDTO contentDTO : approvalList) {
            List<ApprovalUser> users = userList.stream().filter(approvalUser -> approvalUser.getId().equals(contentDTO.getUserId())).collect(Collectors.toList());
            if (users != null && !users.isEmpty()) {
                ApprovalUser user = users.get(0);
                contentDTO.setUserNick(user.getName());
                contentDTO.setUserAvatar(user.getAvatar());
                if (StringUtils.isBlank(contentDTO.getUserAvatar())) {
                    contentDTO.setColor(user.getColor() != null ? user.getColor() : ApproConstants.DEFAULT_COLOR);
                }
            }
            ClientApprovalVO clientApprovalVO = new ClientApprovalVO(contentDTO);
            clientApprovalVOS.add(clientApprovalVO);
        }
    }

    private List<ApproveAttrVO> getDetail(String approvalId) {
        List<ApproveAttributeVO> attrList = approvalAttrMapper.selectAttrList(approvalId);

        List<ApproveAttrVO> attrs = new ArrayList<>();
        for (ApproveAttributeVO attr : attrList) {
            if (attr.getAttrParent() != null) {
                continue;
            }
            Integer type = attr.getAttrType();
            if (type == null) {
                continue;
            }
            if (type == 7) {
                Integer num = null;
                ApproveAttrVO attrVo = new ApproveAttrVO(attr);
                Map<Integer, List<ApproveAttrVO>> map = new HashMap<>(1);
                for (ApproveAttributeVO childAttr : attrList) {
                    if (childAttr.getAttrParent() != null && childAttr.getAttrNum() != null && attr.getId().equals(childAttr.getAttrParent())) {
                        num = childAttr.getAttrNum();
                        List<ApproveAttrVO> childAttrs = map.get(num);
                        if (CollectionUtils.isEmpty(childAttrs)) {
                            childAttrs = new ArrayList<>();
                        }
                        childAttrs.add(new ApproveAttrVO(childAttr));
                        map.put(num, childAttrs);
                    }
                }
                if (MapUtils.isNotEmpty(map)) {
                    List<ApproveRowVO> details = new ArrayList<>(map.size());
                    for (Map.Entry<Integer, List<ApproveAttrVO>> entry : map.entrySet()) {
                        details.add(new ApproveRowVO(entry.getKey(), entry.getValue()));
                    }
                    attrVo.setContents(details);
                    attrVo.setNum(num);
                }
                attrs.add(attrVo);
                Collections.sort(attrs, new Comparator<ApproveAttrVO>() {
                    @Override
                    public int compare(ApproveAttrVO o1, ApproveAttrVO o2) {
                        if (o1.getNum() > o2.getNum()) {
                            return 1;
                        } else if (o1.getNum() < o2.getNum()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
            } else {
                attrs.add(new ApproveAttrVO(attr));
            }
        }
        return attrs;
    }
}
