package com.yunjing.approval.service.impl;

import com.yunjing.approval.model.dto.*;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.service.*;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 刘小鹏
 * @date 2018/04/18
 */
@Service
public class DataTransferServiceImpl implements IDataTransferService {

    @Autowired
    private IApprovalService approvalService;
    @Autowired
    private ICopyService copyService;
    @Autowired
    private ICopysService copysService;
    @Autowired
    private IModelService modelService;
    @Autowired
    private IModelItemService modelItemService;
    @Autowired
    private IOrgModelService orgModelService;
    @Autowired
    private IApprovalAttrService approvalAttrService;
    @Autowired
    private IApprovalProcessService approvalProcessService;
    @Autowired
    private IProcessService processService;
    @Autowired
    private IConditionService conditionService;

    private List<ModelDTO> modelDTOS;

    @Override
    public boolean addApproval(List<ApprovalDTO> dtoList) {
        List<Approval> list = new ArrayList<>();
        for (ApprovalDTO dto : dtoList) {
            Approval approval = new Approval();
            approval.setId(dto.getId());
            approval.setOrgId(dto.getOrgId());
            approval.setUserId(dto.getUserId());
            approval.setModelId(dto.getModelId());
            approval.setResult(dto.getResult());
            approval.setState(dto.getState());
            approval.setCreateTime(dto.getCreateTime());
            approval.setFinishTime(dto.getFinishTime());
            list.add(approval);
        }
        boolean insertBatch = approvalService.insertBatch(list);
        if (!insertBatch) {
            throw new InsertMessageFailureException("批量迁移approval表数据失败");
        }
        return insertBatch;
    }

    @Override
    public boolean addCopy(List<CopyDTO> dtoList) {
        boolean isInserted = false;
        List<Copy> list = new ArrayList<>();
        for (CopyDTO dto : dtoList) {
            Copy copy = new Copy();
            copy.setUserId(dto.getUserId());
            copy.setSort(dto.getSort());
            copy.setModelId(dto.getModelId());
            copy.setType(dto.getType());
            copy.setId(dto.getCopyId());
            list.add(copy);
        }
        if (!list.isEmpty()) {
            isInserted = copyService.insertBatch(list);
            if (!isInserted) {
                throw new InsertMessageFailureException("批量迁移copy表数据失败");
            }
        }
        return isInserted;
    }

    @Override
    public boolean addModel(List<ModelDTO> dtoList) {
        modelDTOS = dtoList;
        boolean isInserted = false;
        List<ModelL> modelLList = new ArrayList<>();
        for (ModelDTO dto : dtoList) {
            ModelL modelL = new ModelL();
            modelL.setId(dto.getModelId());
            modelL.setVisibleRange("全部可见");
            modelL.setIsDisabled(dto.getIsDisabled());
            modelL.setModelVersion(dto.getModelVersion());
            modelL.setSort(dto.getSort());
            modelL.setIntroduce(dto.getIntroduce());
            modelL.setIsDef(dto.getIsDef());
            modelL.setLogo(dto.getLogo());
            modelL.setProvider(dto.getProvider());
            modelL.setModelType(dto.getModelType());
            modelLList.add(modelL);
        }
        if (!modelLList.isEmpty()) {
            isInserted = modelService.insertBatch(modelLList);
            if (!isInserted) {
                throw new InsertMessageFailureException("批量迁移model表数据失败");
            }
        }
        return isInserted;
    }

    @Override
    public boolean addModelItem(List<ModelItemDTO> dtoList) {
        boolean isInserted = false;
        Set<String> mids = modelDTOS.stream().filter(modelDTO -> "请假".equals(modelDTO.getModelName())).map(ModelDTO::getModelId).collect(Collectors.toSet());
        List<ModelItem> modelItemList = new ArrayList<>();
        for (ModelItemDTO dto : dtoList) {

            if (dto.getDataType() == 5) {
                ModelItem modelItem = new ModelItem();
                modelItem.setIsJudge(dto.getIsJudge());
                modelItem.setDataType(dto.getDataType());
                modelItem.setIsJudge(dto.getIsJudge());
                modelItem.setField(dto.getField());
                modelItem.setHelp(dto.getHelp());
                modelItem.setIsChild(dto.getIsChild());
                modelItem.setIsCustom(dto.getIsCustom());
                modelItem.setIsDisplay(dto.getIsDisplay());
                modelItem.setIsRequired(dto.getIsRequired());
                modelItem.setItemVersion(dto.getItemVersion());
                modelItem.setModelId(dto.getModelId());
                modelItem.setOptValue(dto.getOptValue());
                modelItem.setDefValue(dto.getDefValue());
                modelItem.setUnit(dto.getUnit());
                modelItem.setPriority(dto.getPriority());
                modelItem.setId(dto.getModelItemId());
                modelItem.setItemLabels("结束时间");
                modelItem.setItemLabel("开始时间");
                modelItem.setDateFormat("yyyy-MM-dd HH:mm");
                modelItem.setId(dto.getModelItemId());
                modelItemList.add(modelItem);
            } else if (mids.contains(dto.getModelId()) && "附件".equals(dto.getItemLabel())) {
                ModelItem modelItem = new ModelItem();
                modelItem.setDataType(10);
                modelItem.setDateFormat(dto.getDateFormat());
                modelItem.setItemLabel("图片");
                modelItem.setItemLabels("");
                modelItem.setIsJudge(dto.getIsJudge());
                modelItem.setField(dto.getField());
                modelItem.setHelp(dto.getHelp());
                modelItem.setIsChild(dto.getIsChild());
                modelItem.setIsCustom(dto.getIsCustom());
                modelItem.setIsDisplay(dto.getIsDisplay());
                modelItem.setIsRequired(dto.getIsRequired());
                modelItem.setItemVersion(dto.getItemVersion());
                modelItem.setModelId(dto.getModelId());
                modelItem.setOptValue(dto.getOptValue());
                modelItem.setDefValue(dto.getDefValue());
                modelItem.setUnit(dto.getUnit());
                modelItem.setPriority(dto.getPriority());
                modelItem.setId(dto.getModelItemId());
                modelItemList.add(modelItem);
            } else if (dto.getDataType() == 4) {
                ModelItem modelItem = new ModelItem();
                modelItem.setDataType(10);
                modelItem.setDateFormat("yyyy-MM-dd HH:mm");
                modelItem.setItemLabel(dto.getItemLabel());
                modelItem.setItemLabels("");
                modelItem.setIsJudge(dto.getIsJudge());
                modelItem.setField(dto.getField());
                modelItem.setHelp(dto.getHelp());
                modelItem.setIsChild(dto.getIsChild());
                modelItem.setIsCustom(dto.getIsCustom());
                modelItem.setIsDisplay(dto.getIsDisplay());
                modelItem.setIsRequired(dto.getIsRequired());
                modelItem.setItemVersion(dto.getItemVersion());
                modelItem.setModelId(dto.getModelId());
                modelItem.setOptValue(dto.getOptValue());
                modelItem.setDefValue(dto.getDefValue());
                modelItem.setUnit(dto.getUnit());
                modelItem.setPriority(dto.getPriority());
                modelItem.setId(dto.getModelItemId());
                modelItemList.add(modelItem);
            } else {
                ModelItem modelItem = new ModelItem();
                modelItem.setDataType(dto.getDataType());
                modelItem.setDateFormat(dto.getDateFormat());
                modelItem.setItemLabel(dto.getItemLabel());
                modelItem.setItemLabels(dto.getItemLabel());
                modelItem.setIsJudge(dto.getIsJudge());
                modelItem.setField(dto.getField());
                modelItem.setHelp(dto.getHelp());
                modelItem.setIsChild(dto.getIsChild());
                modelItem.setIsCustom(dto.getIsCustom());
                modelItem.setIsDisplay(dto.getIsDisplay());
                modelItem.setIsRequired(dto.getIsRequired());
                modelItem.setItemVersion(dto.getItemVersion());
                modelItem.setModelId(dto.getModelId());
                modelItem.setOptValue(dto.getOptValue());
                modelItem.setDefValue(dto.getDefValue());
                modelItem.setUnit(dto.getUnit());
                modelItem.setPriority(dto.getPriority());
                modelItem.setId(dto.getModelItemId());
                modelItemList.add(modelItem);
            }
        }
        if (!modelItemList.isEmpty()) {
            isInserted = modelItemService.insertBatch(modelItemList);
            if (!isInserted) {
                throw new InsertMessageFailureException("批量迁移model_item表数据失败");
            }
        }
        return isInserted;
    }

    @Override
    public boolean addOrgModel(List<OrgModelDTO> dtoList) {
        boolean isInserted = false;
        List<OrgModel> orgModelList = new ArrayList<>();
        for (OrgModelDTO dto : dtoList) {
            OrgModel orgModel = new OrgModel();
            orgModel.setId(dto.getOrgModelId());
            orgModel.setCreateTime(dto.getCreateTime().getTime());
            orgModel.setDataType(dto.getDataType());
            orgModel.setOrgId(dto.getOrgId());
            orgModel.setModelId(dto.getModelId());
            orgModelList.add(orgModel);
        }
        if (!orgModelList.isEmpty()) {
            isInserted = orgModelService.insertBatch(orgModelList);
            if (!isInserted) {
                throw new InsertMessageFailureException("批量迁移org_model表数据失败");
            }
        }
        return isInserted;
    }

    @Override
    public boolean addApprovalProcess(List<ApprovalProcessDTO> dtoList) {
        boolean isInserted = false;
        List<ApprovalProcess> processList = new ArrayList<>();
        for (ApprovalProcessDTO dto : dtoList) {
            ApprovalProcess approvalProcess = new ApprovalProcess();
            approvalProcess.setId(dto.getProcessId());
            approvalProcess.setProcessState(dto.getState());
            approvalProcess.setProcessTime(dto.getProcessTime().getTime());
            approvalProcess.setApprovalId(dto.getApprovalId());
            approvalProcess.setSeq(dto.getSeq());
            approvalProcess.setUserId(dto.getUserId());
            approvalProcess.setReason(dto.getReason());
            processList.add(approvalProcess);
        }
        if (!processList.isEmpty()) {
            isInserted = approvalProcessService.insertBatch(processList);
            if (isInserted) {
                throw new InsertMessageFailureException("批量迁移approval_process表数据失败");
            }
        }
        return isInserted;
    }

    @Override
    public boolean addSetsCondition(List<SetsConditionDTO> dtoList) {
        boolean isInserted = false;
        List<SetsCondition> conditionList = new ArrayList<>();
        for (SetsConditionDTO dto : dtoList) {
            SetsCondition condition = new SetsCondition();
            condition.setId(dto.getConditions());
            condition.setContent(dto.getContent());
            condition.setCdn(dto.getCdn());
            condition.setEnabled(dto.getEnabled());
            condition.setModelId(dto.getModel());
            condition.setSort(dto.getSort());
            conditionList.add(condition);
        }
        if (!conditionList.isEmpty()) {
            isInserted = conditionService.insertBatch(conditionList);
            if (!isInserted) {
                throw new InsertMessageFailureException("批量迁移approval_sets_condition表数据失败");
            }
        }
        return isInserted;
    }

    @Override
    public boolean addSetsProcess(List<SetsProcessDTO> dtoList) {
        boolean isInserted = false;
        List<SetsProcess> processeList = new ArrayList<>();
        for (SetsProcessDTO dto : dtoList) {
            SetsProcess process = new SetsProcess();
            process.setId(dto.getProcess());
            process.setApprover(dto.getApprover());
            process.setModelId(dto.getModel());
            process.setSort(dto.getSort());
            process.setConditionId(dto.getConditions());
            processeList.add(process);
        }
        if(!processeList.isEmpty()){
            isInserted = processService.insertBatch(processeList);
            if(!isInserted){
                throw new InsertMessageFailureException("批量迁移approval_sets_process表数据失败");
            }
        }
        return isInserted;
    }

    @Override
    public boolean addCopyS(List<CopySDTO> dtoList) {
        boolean isInserted = false;
        List<Copys> copysList = new ArrayList<>();
        for (CopySDTO sdto : dtoList) {
            Copys copys = new Copys();
            copys.setIsRead(1);
            copys.setCopySType(sdto.getCopySType());
            copys.setApprovalId(sdto.getApprovalId());
            copys.setId(sdto.getCopySId());
            copysList.add(copys);
        }
        if (!copysList.isEmpty()) {
            isInserted = copysService.insertBatch(copysList);
            if (!isInserted) {
                throw new InsertMessageFailureException("批量迁移approval_copys 表数据失败");
            }
        }
        return isInserted;
    }

    @Override
    public boolean addApprovalAttr(List<ApprovalAttrDTO> dtoList) {
        boolean isInserted = false;
        List<ApprovalAttr> attrList = new ArrayList<>();
        for (ApprovalAttrDTO dto : dtoList) {
            ApprovalAttr attr = new ApprovalAttr();
            attr.setId(dto.getAttrId());
            attr.setAttrValue(dto.getAttrValue());
            attr.setApprovalId(dto.getApprovalId());
            attr.setAttrName(dto.getAttrName());
            attr.setAttrType(dto.getAttrType());
            attr.setAttrNum(dto.getAttrNum());
            attr.setAttrParent(dto.getAttrParent());
            attrList.add(attr);
        }
        if (!attrList.isEmpty()){
            isInserted = approvalAttrService.insertBatch(attrList);
            if (!isInserted){
                throw new InsertMessageFailureException("批量迁移approval_attr表数据失败");
            }
        }
        return isInserted;
    }
}
