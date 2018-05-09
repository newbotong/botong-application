package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ModelCategoryMapper;
import com.yunjing.approval.model.entity.ModelCategory;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.ModelCategoryVO;
import com.yunjing.approval.model.vo.ModelListVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.service.IModelCategoryService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.mommon.global.exception.MessageNotExitException;
import com.yunjing.mommon.global.exception.MissingRequireFieldException;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
@Service
public class ModelCategoryServiceImpl extends BaseServiceImpl<ModelCategoryMapper, ModelCategory> implements IModelCategoryService {

    @Autowired
    private IModelService modelService;

    @Override
    public boolean createOrEditCategory(String orgId, String categoryId, String categoryName) {
        ModelCategory modelCategory = new ModelCategory();
        if (categoryId == null) {
            modelCategory.setId(IDUtils.uuid());
            modelCategory.setCreateTime(System.currentTimeMillis());
        } else {
            modelCategory = this.selectById(categoryId);
            if (modelCategory == null) {
                modelCategory = new ModelCategory();
                modelCategory.setId(IDUtils.uuid());
                modelCategory.setCreateTime(System.currentTimeMillis());
            }
        }
        modelCategory.setUpdateTime(System.currentTimeMillis());
        modelCategory.setCategoryName(categoryName);
        modelCategory.setOrgId(orgId);
        modelCategory.setSort(0);
        modelCategory.setIsDelete(0);
        return this.insertOrUpdate(modelCategory);
    }

    @Override
    public boolean deleteCategory(String companyId, String categoryId) {
        boolean flag = false;
        List<ModelVO> modelList = modelService.findModelListByOrgId(companyId);
        List<ModelVO> modelVOList = modelList.stream().filter(modelVO -> categoryId.equals(modelVO.getCategoryId())).collect(Collectors.toList());
        if (modelVOList.isEmpty()) {
            flag = this.delete(Condition.create().where("id={0}", categoryId));
        } else {
            // 如果删除的分组中有审批模型，则把这些审批模型的所属分组批量改为其他分组
            List<String> modelIds = new ArrayList<>();
            for (ModelVO modelVO : modelVOList) {
                modelIds.add(modelVO.getModelId());
            }
            List<ModelL> modelLS = modelService.selectList(Condition.create().in("id", modelIds));
            ModelCategory modelCategory = this.selectOne(Condition.create().where("category_name={0}", "其他").and("org_id={0}", companyId));
            for (ModelL modelL : modelLS) {
                modelL.setCategoryId(modelCategory.getId());
            }
            boolean isUpdated = modelService.updateBatchById(modelLS);
            if (isUpdated) {
                flag = this.delete(Condition.create().where("id={0}", categoryId));
            }
        }
        return flag;
    }

    @Override
    public boolean sortedCategory(String orgId, String sortArray) throws Exception {
        boolean isUpdated = false;
        try {
            // 解析排序数据
            JSONArray sortJSONArray = JSONArray.parseArray(sortArray);
            // 封装排序集合为KEY-VALUE以部门编号为KEY，位置为VALUE
            Map<String, Integer> categorySortMap = new HashMap<>(sortJSONArray.size());
            for (int i = 0; i < sortJSONArray.size(); i++) {
                JSONObject sortJSON = sortJSONArray.getJSONObject(i);
                categorySortMap.put(sortJSON.getString("categoryId"), sortJSON.getInteger("sort"));
            }
            // 查询当前企业的所有分组
            List<ModelCategory> modelCategoryList = this.selectList(Condition.create().where("org_id = {0}", orgId));
            if (modelCategoryList != null && !modelCategoryList.isEmpty()) {
                for (ModelCategory modelCategory : modelCategoryList) {
                    Integer sort = categorySortMap.get(modelCategory.getId());
                    modelCategory.setSort(sort);
                }
                isUpdated = this.updateBatchById(modelCategoryList);
                if (isUpdated == false) {
                    throw new UpdateMessageFailureException("更新部门排序信息失败");
                }
            } else {
                throw new MessageNotExitException("当前企业下不存在分组");
            }
        } catch (Exception e) {
            throw new MissingRequireFieldException("解析分组排序数据错误");
        }
        return isUpdated;
    }

    @Override
    public List<ModelCategoryVO> getCategoryList(String orgId) {
        List<ModelCategoryVO> modelCategoryVOList = new ArrayList<>();
        List<ModelListVO> modelList = modelService.findModelList(orgId);
        for (ModelListVO modelListVO : modelList) {
            ModelCategoryVO modelCategoryVO = new ModelCategoryVO();
            modelCategoryVO.setCategoryId(modelListVO.getCategoryId());
            modelCategoryVO.setCategoryName(modelListVO.getCategroyName());
            modelCategoryVO.setModelCount(modelListVO.getModelVOList().size());
            modelCategoryVO.setSort(modelListVO.getSort());
            modelCategoryVO.setUpdateTime(modelListVO.getUpdateTime());
            modelCategoryVOList.add(modelCategoryVO);
        }
        Collections.sort(modelCategoryVOList, new Comparator<ModelCategoryVO>() {
            @Override
            public int compare(ModelCategoryVO o1, ModelCategoryVO o2) {
                if (o1.getSort() > o2.getSort()) {
                    return 1;
                } else if (o1.getSort().equals(o2.getSort()) && o1.getUpdateTime() > o2.getUpdateTime()) {
                    return -1;
                } else if (o1.getSort().equals(o2.getSort())) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        return modelCategoryVOList;
    }

    @Override
    public boolean initModelCategory(String companyId) {
        boolean isUpdated = false;
        // 查询系统分组
        List<ModelCategory> defaultCategoryList = this.selectList(Condition.create().where("is_default={0}", 1));
        List<ModelCategory> newCategoryList = new ArrayList<>();
        for (ModelCategory category : defaultCategoryList) {
            ModelCategory newCategory = new ModelCategory();
            newCategory.setId(IDUtils.uuid());
            newCategory.setOrgId(companyId);
            newCategory.setSort(category.getSort());
            newCategory.setCategoryName(category.getCategoryName());
            newCategory.setIsDefault(0);
            newCategory.setCreateTime(category.getCreateTime());
            newCategory.setUpdateTime(category.getUpdateTime());
            newCategoryList.add(newCategory);
        }
        boolean insertBatch = this.insertBatch(newCategoryList);
        if (!insertBatch) {
            logger.error("批量插入分组失败");
        }
        List<ModelL> modelLList = modelService.findModel(companyId);
        // 将已经初始化好的model进行分组
        // 出勤休假
        String modelName1 = "请假,出差，外出，加班";
        // 人事
        String modelName2 = "转正申请，招聘申请";
        // 行政
        String modelName3 = "用车申请，物品领用，用印申请，通用审批";
        // 财务
        String modelName4 = "报销，备用金申请";
        modelLList.forEach(modelL -> {
            if (StringUtils.isBlank(modelL.getCategoryId())) {
                if (modelName1.contains(modelL.getModelName())) {
                    List<String> ids = newCategoryList.stream().filter(modelCategory -> modelCategory.getCategoryName().equals("出勤休假")).map(ModelCategory::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        modelL.setCategoryId(ids.get(0));
                    }
                } else if (modelName2.contains(modelL.getModelName())) {
                    List<String> ids = newCategoryList.stream().filter(modelCategory -> modelCategory.getCategoryName().equals("人事")).map(ModelCategory::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        modelL.setCategoryId(ids.get(0));
                    }
                } else if (modelName3.contains(modelL.getModelName())) {
                    List<String> ids = newCategoryList.stream().filter(modelCategory -> modelCategory.getCategoryName().equals("行政")).map(ModelCategory::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        modelL.setCategoryId(ids.get(0));
                    }
                } else if (modelName4.contains(modelL.getModelName())) {
                    List<String> ids = newCategoryList.stream().filter(modelCategory -> modelCategory.getCategoryName().equals("财务")).map(ModelCategory::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        modelL.setCategoryId(ids.get(0));
                    }
                } else {
                    List<String> ids = newCategoryList.stream().filter(modelCategory -> modelCategory.getCategoryName().equals("其他")).map(ModelCategory::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        modelL.setCategoryId(ids.get(0));
                    }
                }
            }
        });
        isUpdated = modelService.updateBatchById(modelLList);
        return isUpdated;
    }
}
