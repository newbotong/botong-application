package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.common.mybatis.page.Page;
import com.yunjing.approval.config.RedisApproval;
import com.yunjing.approval.dao.mapper.*;
import com.yunjing.approval.model.dto.ApprovalContentDTO;
import com.yunjing.approval.model.dto.ApprovalDetailDTO;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.param.FilterParam;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.processor.task.async.ApprovalPushTask;
import com.yunjing.approval.service.*;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private ApprovalUserMapper approvalUserMapper;
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

    @Autowired
    private RedisApproval redisApproval;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ClientModelVO> getList(String orgId) {
        List<ModelL> modelLList = modelMapper.selectModelListByOrgId(orgId);
        List<ClientModelVO> list = new ArrayList<>();
        for (ModelL model : modelLList) {
            ClientModelVO modelVO1 = new ClientModelVO(model);
            list.add(modelVO1);
        }
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Page<ClientApprovalVO> getWaited(Page page, String companyId, String memberId, FilterParam filterParam) {

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
        convertList(clientApprovalVOS, waitedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS != null ? clientApprovalVOS : new ArrayList<>());
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Page<ClientApprovalVO> getCompleted(Page page, String orgId, String userId, FilterParam filterParam) {
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
        userIds.add(userId);
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> completedMeApprovalList = approvalProcessMapper.getCompletedApprovalList(index, size, orgId, userIds, filterParam);
        convertList(clientApprovalVOS, completedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS != null ? clientApprovalVOS : new ArrayList<>());
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getLaunched(Page page, String orgId, String userId, FilterParam filterParam) {
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> launchedMeApprovalList = approvalProcessMapper.getLaunchedApprovalList(index, size, orgId, userId, filterParam);
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());
        String message = "";
        int i = 1;
        ApprovalUser user = new ApprovalUser();
        for (ApprovalContentDTO contentVO : launchedMeApprovalList) {
            List<ApprovalUser> users = userList.stream().filter(approvalUser -> approvalUser.getId().equals(contentVO.getUserId())).collect(Collectors.toList());
            for (ApprovalUser approvalUser : users) {
                user = approvalUser;
            }
            if (contentVO.getProcessState() == 0 && contentVO.getState() == 0) {
                int critical = i++;
                String name = StringUtils.isNotBlank(user.getName()) ? user.getName() : "";
                if (name.length() > 2) {
                    message = "等待" + name.substring(1, 3) + "审批";
                } else {
                    message = "等待" + name + "审批";
                }
                if (critical == 1) {
                    contentVO.setMessage(message);
                }
            } else if (contentVO.getState() == 1) {
                message = "审批完成";
                if (contentVO.getResult() != null && contentVO.getResult() == 1) {
                    message += " （同意）";
                } else {
                    message += " （拒绝）";
                }
            } else if (contentVO.getState() == 2) {
                message = "已撤回";
            }
            contentVO.setMessage(message);
        }
        convertList(clientApprovalVOS, launchedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS != null ? clientApprovalVOS : new ArrayList<>());
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getCopied(Page page, String orgId, String userId, FilterParam filterParam) {
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> copyApprovalList = copysMapper.getCopiedApprovalList(index, size, orgId, userId, filterParam);
        convertList(clientApprovalVOS, copyApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS != null ? clientApprovalVOS : new ArrayList<>());
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public ClientApprovalDetailVO getApprovalDetail(String orgId, String memberId, String approvalId) {
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
            ModelL modelL = modelService.selectOne(Condition.create().where("id={0}", approvalById.getModelId()));
            clientApprovalDetailVO.setModelName(modelL.getModelName() != null ? modelL.getModelName() : null);
            clientApprovalDetailVO.setDeptName(approvalById.getDeptName());
            clientApprovalDetailVO.setPosition(approvalById.getPosition());
            if (StringUtils.isNotBlank(approvalById.getAvatar())) {
                clientApprovalDetailVO.setAvatar(approvalById.getAvatar());
            } else {
                clientApprovalDetailVO.setColor("");
            }
            clientApprovalDetailVO.setState(approvalById.getState());
            clientApprovalDetailVO.setResult(approvalById.getResult() != null ? approvalById.getResult() : null);
        }
        // 获取审批人信息
        List<ApprovalUserVO> approvalUserList = approvalProcessMapper.getApprovalUserList(approvalId);
        int index = 1;
        for (ApprovalUserVO approvalUserVO : approvalUserList) {
            approvalUserVO.setColor(approvalUserVO.getColor() != null ? approvalUserVO.getColor() : "#1E90FF");
            if (StringUtils.isBlank(approvalUserVO.getAvatar())) {
                approvalUserVO.setColor(approvalUserVO.getColor() != null ? approvalUserVO.getColor() : "#1E90FF");
                approvalUserVO.setAvatarName(approvalUserVO.getName().length() <= 2 ? approvalUserVO.getName() : approvalUserVO.getName().substring(1, 3));
            }
            if (approvalUserVO.getProcessState() == 0) {
                if (approvalUserVO.getUserId().equals(memberId)) {
                    //描述提醒用户信息
                    clientApprovalDetailVO.setProcessState(approvalUserVO.getProcessState());
                    clientApprovalDetailVO.setMessage("等待我审批");
                } else {
                    int flag = index++;
                    //描述提醒用户信息
                    if (flag == 1) {
                        clientApprovalDetailVO.setMessage("等待" + approvalUserVO.getName() + "审批");
                    }
                }
            }
        }
        clientApprovalDetailVO.setApprovalUserList(approvalUserList);

        // 获取抄送人信息
        List<CopyUserVO> copyUserList = copysMapper.getCopyUserList(approvalId);
        copyUserList.forEach(copyUserVO -> {
            if (StringUtils.isBlank(copyUserVO.getAvatar())) {
                copyUserVO.setColor(copyUserVO.getColor() != null ? copyUserVO.getColor() : "#1E90FF");
                copyUserVO.setAvatarName(copyUserVO.getName().length() <= 2 ? copyUserVO.getName() : copyUserVO.getName().substring(1, 3));
            }
        });
        // 注入抄送人信息
        clientApprovalDetailVO.setCopyUserList(copyUserList);
        return clientApprovalDetailVO;
    }

    @Override
    public boolean solveApproval(String orgId, String userId, String approvalId, Integer state) {
        boolean flag = false;
        List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().where("approval_id={0}", approvalId));
        if (processList != null && !processList.isEmpty()) {
            for (ApprovalProcess process : processList) {
                // 当审批流程中的审批未处理时（0：未处理）
                if (process.getProcessState() == 0) {
                    if (process.getUserId().equals(userId)) {
                        process.setProcessState(state);
                        process.setProcessTime(System.currentTimeMillis());
                        boolean update = approvalProcessService.update(process, Condition.create().where("approval_id={0}", approvalId));
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
            approvalPushTask.init(approvalId, orgId, userId).run();
        }
        return flag;
    }

    @Override
    public boolean revokeApproval(String orgId, String userId, String approvalId) {
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
            flag = approvalService.updateById(approval);
            if (!flag) {
                throw new UpdateMessageFailureException("审批撤销操作--修改审批信息失败");
            }
        }
        return flag;
    }

    @Override
    public boolean transferApproval(String orgId, String userId, String transferredUserId, String approvalId) {

        List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().where("approval_id={0}", approvalId));
        int num = 0;
        List<ApprovalProcess> newProcessList = new ArrayList<>();
        for (ApprovalProcess approvalProcess : processList) {
            if (approvalProcess.getProcessState() == 0 && num == 0) {
                approvalProcess.setProcessState(3);
                approvalProcess.setProcessTime(System.currentTimeMillis());
                ApprovalProcess newProcess = new ApprovalProcess();
                newProcess.setSeq(approvalProcess.getSeq() + 1);
                newProcess.setId(IDUtils.uuid());
                newProcess.setUserId(transferredUserId);
                newProcess.setProcessState(0);
                newProcess.setApprovalId(approvalId);
                newProcess.setProcessTime(System.currentTimeMillis());
                newProcessList.add(newProcess);
                num++;
                continue;
            }
            if (num == 1) {
                approvalProcess.setSeq(approvalProcess.getSeq() + 1);
            }
        }
        boolean insertBatch = approvalProcessService.insertBatch(newProcessList);
        if (!insertBatch) {
            throw new InsertMessageFailureException("转让审批操作--新增审批流程信息失败");
        }
        boolean batchById = approvalProcessService.updateBatchById(processList);
        if (!batchById) {
            throw new UpdateMessageFailureException("转让审批操作--修改审批流程信息失败");
        }
        //转让成功推送下一个
        if (batchById) {
            approvalPushTask.init(approvalId, orgId, userId).run();
        }
        return batchById;
    }

    @Override
    public boolean updateCopyReadState(String[] approvalId) {
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
                    contentDTO.setColor(user.getColor() != null ? user.getColor() : "#1E90FF");
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
                ApproveAttrVO attrVo = new ApproveAttrVO(attr);
                Map<Integer, List<ApproveAttrVO>> map = new HashMap<>(1);
                for (ApproveAttributeVO childAttr : attrList) {
                    if (childAttr.getAttrParent() == null) {
                        continue;
                    }

                    if (attr.getId().equals(childAttr.getAttrParent())) {
                        Integer num = childAttr.getAttrNum();
                        if (num == null) {
                            continue;
                        }
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
                }
                attrs.add(attrVo);
            } else {
                attrs.add(new ApproveAttrVO(attr));
            }
        }
        return attrs;
    }
}
