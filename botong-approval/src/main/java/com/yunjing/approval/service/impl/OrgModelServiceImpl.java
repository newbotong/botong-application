package com.yunjing.approval.service.impl;


import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.OrgModelMapper;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.model.entity.ModelItem;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.entity.OrgModel;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.service.IOrgModelService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业模型实现类
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Service
public class OrgModelServiceImpl extends BaseServiceImpl<OrgModelMapper, OrgModel> implements IOrgModelService {

    @Autowired
    private IModelService modelLService;

    @Autowired
    private IModelItemService modelItemService;

    @Autowired
    private IApprovalService approvalService;

    private static final Log logger = LogFactory.getLog(OrgModelServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean createApprovalModel(String orgId) {
        boolean flag;
        // 查询系统的审批模板
        List<ModelL> defaultModelList = modelLService.selectList(Condition.create().where("is_def={0}", ApproConstants.IS_SYSTEM_MODEL_1));
        //查询该企业下是否已经有审批模板
        List<ModelL> modelListByOrgId = modelLService.findModel(orgId);
        if (modelListByOrgId != null && modelListByOrgId.size() > 0) {
            flag = false;
        } else {
            // 初始化该企业审批模板
            try {
                flag = this.createOrgModel(orgId, defaultModelList);
            } catch (Exception e) {
                flag = true;
                logger.error("初始化该企业审批模板失败", e);
            }
        }
        return flag;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteApprovalModel(String orgId) {
        boolean flag = false;
        // 查询审批模板使用后产生的数据
        List<Approval> approvalList = approvalService.selectList(Condition.create().where("org_id={0}", orgId));
        if (approvalList != null && approvalList.size() > 0) {
            // 记录有没有审批完成的数据条数;
            int count = 0;
            for (Approval approval : approvalList) {
                if (approval.getState() == ApproConstants.APPROVAL_STATE_1) {
                    count++;
                }
            }
            if (count == 0) {
                // 删除企业模板
                flag = this.deleteOrgModel(orgId);
            }
        } else {
            // 删除企业模板
            flag = this.deleteOrgModel(orgId);
        }

        return flag;
    }


    /**
     * 插入企业审批模板
     *
     * @param orgId            企业主键
     * @param defaultModelList 系统默认模板
     * @return List<String> 返回模板ID集合（modelIds）
     * @throws Exception
     */
    private boolean createOrgModel(String orgId, List<ModelL> defaultModelList) {
        boolean isInsert = false;
        List<ModelL> newModelList = new ArrayList<>();
        List<OrgModel> newOrgModelList = new ArrayList<>();
        for (ModelL model : defaultModelList) {

            // 插入新Model
            ModelL newModel = new ModelL();
            newModel.setId(IDUtils.uuid());
            newModel.setModelName(model.getModelName());
            newModel.setLogo(model.getLogo());
            newModel.setIntroduce(model.getIntroduce());
            newModel.setSort(model.getSort());
            newModel.setIsDisabled(model.getIsDisabled());
            newModel.setIsDef(ApproConstants.IS_SYSTEM_MODEL_0);
            newModel.setProvider(model.getProvider());
            newModel.setModelType(model.getModelType());
            newModel.setModelVersion(model.getModelVersion());
            newModelList.add(newModel);
            isInsert = modelLService.insert(newModel);

            // 插入新OrgModel
            OrgModel newOrgModel = new OrgModel();
            newOrgModel.setId(IDUtils.uuid());
            newOrgModel.setModelId(newModel.getId());
            newOrgModel.setOrgId(orgId);
            newOrgModel.setDataType(newModel.getModelType());
            newOrgModel.setCreateTime(System.currentTimeMillis());
            newOrgModelList.add(newOrgModel);
            // 查询系统模板子项
            List<ModelItem> defaultModelItemList = modelItemService.selectList(Condition.create().where("model_id={0}", model.getId()).groupBy("field"));
            // 创建该企业模板子项
            this.createModelItem(newModel.getId(), defaultModelItemList);
        }

        if (isInsert && newOrgModelList != null && newOrgModelList.size() > 0) {
            isInsert = this.insertBatch(newOrgModelList);
            if (!isInsert) {
                try {
                    throw new BaseException("企业的审批模板(org_model)批量插入失败");
                } catch (BaseException e) {
                    logger.error("企业的审批模板(org_model)批量插入失败", e);
                }
            }
        } else {
            try {
                throw new BaseException("企业的审批模板(model)批量插入失败");
            } catch (BaseException e) {
                logger.error("企业的审批模板(model)批量插入失败", e);
            }
        }
        return isInsert;
    }

    /**
     * 根据模板插入模板子项
     *
     * @param modelId              该企业模板Id
     * @param defaltModelItemsList 系统模板子项
     * @return boolean
     * @throws Exception
     */
    private boolean createModelItem(String modelId, List<ModelItem> defaltModelItemsList) {
        boolean flag = false;
        if (defaltModelItemsList != null && defaltModelItemsList.size() > 0) {

            String modelItemId = "";
            for (ModelItem modelItem : defaltModelItemsList) {
                if (StringUtils.isNotBlank(modelItem.getIsChild())) {
                    modelItemId = modelItem.getIsChild();
                    break;
                }
            }
            String parent = "";
            for (ModelItem modelItem : defaltModelItemsList) {
                if (StringUtils.isBlank(modelItem.getIsChild())) {

                    ModelItem newModelItem = new ModelItem();
                    newModelItem.setId(IDUtils.uuid());
                    newModelItem.setModelId(modelId);
                    newModelItem.setField(modelItem.getField());
                    newModelItem.setItemLabel(modelItem.getItemLabel());
                    newModelItem.setItemLabels(modelItem.getItemLabels());
                    newModelItem.setPriority(modelItem.getPriority());
                    newModelItem.setDefValue(modelItem.getDefValue());
                    newModelItem.setOptValue(modelItem.getOptValue());
                    newModelItem.setHelp(modelItem.getHelp());
                    newModelItem.setUnit(modelItem.getUnit());
                    newModelItem.setDateFormat(modelItem.getDateFormat());
                    newModelItem.setDataType(modelItem.getDataType());
                    newModelItem.setIsCustom(modelItem.getIsCustom());
                    newModelItem.setIsDisplay(modelItem.getIsDisplay());
                    newModelItem.setIsRequired(modelItem.getIsRequired());
                    newModelItem.setIsJudge(modelItem.getIsJudge());
                    newModelItem.setIsChild(null);
                    newModelItem.setItemVersion(modelItem.getItemVersion());
                    flag = modelItemService.insert(newModelItem);

                    // 此模板子项是其他模板子项的子项情况
                    if (StringUtils.isNotBlank(modelItemId) && modelItem.getId().equals(modelItemId)) {
                        parent = newModelItem.getId();
                    }
                }
            }

            if (StringUtils.isNotBlank(parent)) {
                for (ModelItem modelItem : defaltModelItemsList) {
                    if (StringUtils.isNotBlank(modelItem.getIsChild())) {

                        ModelItem newModelItem = new ModelItem();
                        newModelItem.setId(IDUtils.uuid());
                        newModelItem.setModelId(modelId);
                        newModelItem.setField(modelItem.getField());
                        newModelItem.setItemLabel(modelItem.getItemLabel());
                        newModelItem.setItemLabels(modelItem.getItemLabels());
                        newModelItem.setPriority(modelItem.getPriority());
                        newModelItem.setDefValue(modelItem.getDefValue());
                        newModelItem.setOptValue(modelItem.getOptValue());
                        newModelItem.setHelp(modelItem.getHelp());
                        newModelItem.setUnit(modelItem.getUnit());
                        newModelItem.setDateFormat(modelItem.getDateFormat());
                        newModelItem.setDataType(modelItem.getDataType());
                        newModelItem.setIsCustom(modelItem.getIsCustom());
                        newModelItem.setIsDisplay(modelItem.getIsDisplay());
                        newModelItem.setIsRequired(modelItem.getIsRequired());
                        newModelItem.setIsJudge(modelItem.getIsJudge());
                        newModelItem.setIsChild(parent);
                        newModelItem.setItemVersion(modelItem.getItemVersion());
                        flag = modelItemService.insert(newModelItem);
                    }
                }
            }
        }
        if (!flag) {
            try {
                throw new BaseException("该企业的模板子项初始化失败");
            } catch (BaseException e) {
                logger.error("该企业的模板子项初始化失败", e);
            }
        }
        return true;
    }

    /**
     * @param orgId 企业主键
     * @return
     */
    private boolean deleteOrgModel(String orgId) {
        boolean flag = false;
        // 删除org_model数据
        boolean isDelete = this.delete(Condition.create().where("org_id={0}", orgId));
        if (isDelete) {
            // 删除modelItem数据
            boolean b = modelItemService.deleteModelItemListByOrgId(orgId);
            if (b) {
                // 删除model数据
                flag = modelLService.deleteModel(orgId);
            }
        }
        return flag;
    }
}



