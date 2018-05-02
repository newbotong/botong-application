package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalAttrMapper;
import com.yunjing.approval.dao.mapper.ApprovalMapper;
import com.yunjing.approval.dao.mapper.ModelItemMapper;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.model.vo.ApproveAttributeVO;
import com.yunjing.approval.service.*;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
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
import java.util.stream.Stream;

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
    private ModelItemMapper modelItemMapper;

    @Autowired
    private IApprovalUserService approvalUserService;

    @Autowired
    private IApprovalProcessService approvalProcessService;

    @Autowired
    private ApprovalAttrMapper attrMapper;
    @Autowired
    private IApprovalAttrService attrService;

    @Override
    public List<Approval> repairTitle(String companyId) {
        List<Approval> list = this.selectList(Condition.create().where("org_id={0}",companyId));
        List<ModelL> modelList = modelService.findModel(companyId);
        List<ApprovalUser> approvalUserList = approvalUserService.selectList(Condition.create().where("org_id={0}",companyId));
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

    @Override
    public List<Approval> repairDeptId(String companyId) {
        List<Approval> approvalList = this.selectList(Condition.create().where("org_id={0}",companyId));
        List<ModelItem> modelItemList = modelItemMapper.selectAll(companyId);
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create().where("org_id={0}", companyId));
        List<ApprovalAttr> attrList = attrMapper.selectAttrByOrgId(companyId);
        for (Approval approval : approvalList) {
            List<String> deptIds = userList.parallelStream().filter(approvalUser -> approvalUser.getOrgId().equals(approval.getOrgId()))
                    .filter(approvalUser -> approvalUser.getId().equals(approval.getUserId()))
                    .map(ApprovalUser::getDeptId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deptIds)){
                String[] deptId = deptIds.get(0).split(",");
                approval.setDeptId(deptId[0]);
            }
            List<Integer> modelVersions = modelItemList.parallelStream().filter(modelItem -> modelItem.getModelId().equals(approval.getModelId())).map(ModelItem::getItemVersion).collect(Collectors.toList());
            List<ModelItem> items = modelItemList.parallelStream().filter(modelItem -> modelItem.getModelId().equals(approval.getModelId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(modelVersions)){
                approval.setModelVersion(modelVersions.get(0));
            }
            if (CollectionUtils.isNotEmpty(items)){
                for (ModelItem item : items) {
                    attrList.parallelStream().filter(approvalAttr -> approvalAttr.getApprovalId().equals(approval.getId()))
                            .filter(approvalAttr -> approvalAttr.getAttrName().equals(item.getField()))
                            .forEach(approvalAttr -> approvalAttr.setAttrType(item.getDataType()));
                }
            }
        }
        if(CollectionUtils.isNotEmpty(approvalList)){
            boolean b = this.updateBatchById(approvalList);
            if (!b){
                throw new UpdateMessageFailureException("修复所属部门id失败");
            }
        }
        if (CollectionUtils.isNotEmpty(attrList)){
            boolean b1 = attrService.updateBatchById(attrList);
            if (!b1){
                throw new UpdateMessageFailureException("修复所属部门id失败");
            }
        }
        return approvalList;
    }

}
