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
import com.yunjing.approval.util.DateUtil;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.global.exception.MessageNotExitException;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import com.yunjing.mommon.utils.IDUtils;
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
            modelCategory.setCreateTime(DateUtil.getCurrentTime().getTime());
        } else {
            modelCategory = this.selectById(categoryId);
            if (modelCategory == null) {
                modelCategory = new ModelCategory();
                modelCategory.setId(IDUtils.uuid());
                modelCategory.setCreateTime(DateUtil.getCurrentTime().getTime());
            }
        }
        modelCategory.setUpdateTime(DateUtil.getCurrentTime().getTime());
        modelCategory.setCategoryName(categoryName);
        modelCategory.setOrgId(orgId);
        modelCategory.setSort(0);
        modelCategory.setIsDelete(0);
        return this.insertOrUpdate(modelCategory);
    }

    @Override
    public boolean deleteCategory(String orgId, String categoryId) {
        boolean flag = false;
        List<ModelVO> modelList = modelService.findModelListByOrgId(orgId);
        List<ModelVO> modelVOList = modelList.stream().filter(modelVO -> categoryId.equals(modelVO.getCategoryId())).collect(Collectors.toList());
        if (modelVOList.isEmpty()) {
            flag = this.delete(Condition.create().where("id={0}", categoryId));
        } else {
            // 如果删除的分组中有审批模型，则把这些审批模型的所属分组批量改为其他分组
            List<String> modelIds = new ArrayList<>();
            for (ModelVO modelVO : modelVOList) {
                modelIds.add(modelVO.getModelId());
            }
            List<ModelL> modelLS = modelService.selectList(Condition.create().in("id",modelIds));
            ModelCategory modelCategory = this.selectOne(Condition.create().where("category_name={0}", "其他"));
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
    public boolean sortedCategory(String orgId, String sortArray) throws BaseException {
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
            throw new BaseException("解析分组排序数据错误");
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
}
