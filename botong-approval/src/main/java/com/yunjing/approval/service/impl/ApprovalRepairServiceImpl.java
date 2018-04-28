package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalMapper;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.model.entity.ApprovalProcess;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.service.IApprovalProcessService;
import com.yunjing.approval.service.IApprovalRepairService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.IModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 刘小鹏
 * @date 2018/1/15
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ApprovalRepairServiceImpl extends BaseServiceImpl<ApprovalMapper, Approval> implements IApprovalRepairService {

    @Autowired
    private IModelService modelService;

    @Autowired
    private IApprovalUserService approvalUserService;

    @Autowired
    private IApprovalProcessService approvalProcessService;

    @Override
    public List<Approval> repairTitle(String companyId) {
        List<Approval> list = this.selectList(Condition.create().where("org_id={0}",companyId));
        List<ModelL> modelList = modelService.findModel(companyId);
        List<ApprovalUser> approvalUserList = approvalUserService.selectList(Condition.create());
        if (CollectionUtils.isNotEmpty(list)) {
            List<Approval> entityList = new ArrayList<>();
            for (Approval approval : list) {
                if (StringUtils.isNotBlank(approval.getTitle())) {
                    continue;
                }
                List<ModelL> modelLList = modelList.stream().filter(modelL -> modelL.getId().equals(approval.getModelId())).collect(Collectors.toList());
                List<ApprovalUser> userList = approvalUserList.stream().filter(approvalUser -> approvalUser.getId().equals(approval.getUserId())).collect(Collectors.toList());
                String title = "不存在的";
                if (CollectionUtils.isEmpty(userList)) {
                    title += "用户";
                } else if (CollectionUtils.isEmpty(modelLList)) {
                    title += "模型";
                } else {
                    String nick = userList.get(0).getName();
                    String name = modelLList.get(0).getModelName();
                    if (StringUtils.isBlank(nick)) {
                        title += "用户昵称";
                    } else if (StringUtils.isBlank(name)) {
                        title += "模型名称";
                    } else {
                        title = nick + "的" + name;
                    }
                }
                approval.setTitle(title);
                entityList.add(approval);
            }
            if (CollectionUtils.isNotEmpty(entityList)) {
                this.updateBatchById(entityList);
            }
            return entityList;
        }
        return list;
    }

    @Override
    public List<Approval> repairFinishTime(String companyId) {
        List<Approval> list = this.selectList(Condition.create().where("org_id={0}",companyId).in("state", "1, 2"));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Approval> entityList = new ArrayList<>();
            // 小规模数据修复
            for (Approval approval : list) {
                if (approval.getFinishTime() != null) {
                    continue;
                }
                List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().eq("approval_id", approval.getId()).orderBy("seq", true));
                if (CollectionUtils.isEmpty(processList)) {
                    continue;
                }
                ApprovalProcess process = processList.get(processList.size() - 1);
                if (process == null) {
                    continue;
                }
                approval.setFinishTime(process.getProcessTime());
                entityList.add(approval);
            }
            if (CollectionUtils.isNotEmpty(entityList)) {
                this.updateBatchById(entityList);
            }
            return entityList;
        }
        return null;
    }
}
