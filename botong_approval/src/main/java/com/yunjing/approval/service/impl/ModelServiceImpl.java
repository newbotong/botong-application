package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ConditionMapper;
import com.yunjing.approval.dao.mapper.ModelLMapper;
import com.yunjing.approval.model.entity.ModelCategory;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.ModelListVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.service.*;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.global.exception.MessageNotExitException;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
@Service
public class ModelServiceImpl extends BaseServiceImpl<ModelLMapper, ModelL> implements IModelService {

    @Autowired
    private ModelLMapper modelLMapper;

    @Autowired
    private ICopyService copyService;

    @Autowired
    private IModelCategoryService modelCategoryService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ModelListVO> findModelList(String orgId) {
        List<ModelVO> modelVOList = modelLMapper.selectModelListByOrgId(orgId);
        List<ModelListVO> modelListVOList = new ArrayList<>();
        List<ModelCategory> list = modelCategoryService.selectList(Condition.create().where("org_id={0}", orgId));
        if (!list.isEmpty()) {
            for (ModelCategory modelCategory : list) {
                ModelListVO modelListVO = new ModelListVO();
                modelListVO.setCategoryId(modelCategory.getCategoryId());
                modelListVO.setCategroyName(modelCategory.getCategoryName());
                modelListVO.setSort(modelCategory.getSort());
                modelListVO.setUpdateTime(modelCategory.getUpdateTime());
                if (modelCategory.getCategoryId() != null) {
                    List<ModelVO> modelVOS = modelVOList.stream().filter(modelVO1 -> modelCategory.getCategoryId().equals(modelVO1.getCategoryId())).collect(Collectors.toList());
                    modelListVO.setModelVOList(modelVOS);
                    modelListVO.setModelCount(modelVOS.size());
                }
                modelListVOList.add(modelListVO);
            }
        }
        Collections.sort(modelListVOList, new Comparator<ModelListVO>() {
            @Override
            public int compare(ModelListVO o1, ModelListVO o2) {
                if (o1.getSort() > o2.getSort()) {
                    return 1;
                } else if (o1.getSort().equals(o2.getSort()) && o1.getUpdateTime() < o2.getUpdateTime()) {
                    return 1;
                } else if (o1.getSort().equals(o2.getSort())) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        return modelListVOList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean sortedModel(String categoryId, String sortArray) throws Exception {
        boolean isUpdated = false;
        Map<String, Integer> modelSortMap = null;
        try {
            // 解析排序数据
            JSONArray sortJSONArray = JSONArray.parseArray(sortArray);
            // 封装排序集合为KEY-VALUE以部门编号为KEY，位置为VALUE
            modelSortMap = new HashMap<>(sortJSONArray.size());
            for (int i = 0; i < sortJSONArray.size(); i++) {
                JSONObject sortJSON = sortJSONArray.getJSONObject(i);
                modelSortMap.put(sortJSON.getString("modelId"), sortJSON.getInteger("sort"));
            }
        } catch (Exception e) {
            throw new BaseException("解析分组排序数据错误");
        }

        // 查询当前企业的所有分组
        List<ModelL> modelList = this.selectList(Condition.create().where("category_id = {0}", categoryId));
        if (modelList != null && !modelList.isEmpty()) {
            for (ModelL model : modelList) {
                Integer sort = modelSortMap.get(model.getModelId());
                model.setSort(sort);
            }
            isUpdated = this.updateBatchById(modelList);
            if (isUpdated == false) {
                throw new UpdateMessageFailureException("更新部门排序信息失败");
            }
        } else {
            throw new MessageNotExitException("当前企业下不存在分组");
        }

        return isUpdated;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean moveModel(Long categoryId, String modelId) throws Exception {

        ModelL modelL = this.selectById(modelId);
        if(modelL == null){
            throw new MessageNotExitException("模型不存在");
        }
        modelL.setCategoryId(categoryId);
        boolean updated = this.update(modelL, Condition.create().where("model_id={0}", modelId));
        return updated;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ModelVO> findModelListByOrgId(String orgId) {
        List<ModelVO> modelVOList = modelLMapper.selectModelListByOrgId(orgId);
        for (ModelVO modelVO : modelVOList) {
            // 获取每个审批项的抄送人数量
            int count = copyService.selectCount(Condition.create().where("model_id={0}", modelVO.getModelId()));
            modelVO.setCopyUserCount(count);
        }
        return modelVOList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateVisibleRange(String modelId, String deptIds, String userIds) throws Exception {
        // TODO 调用rpc接口处理可见范围权限业务

        ModelL modelL = this.selectById(modelId);

        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateIsDisabled(String modelId, Integer isDisabled) throws Exception {
        ModelL modelL = this.selectById(modelId);
        if (modelL == null) {
            throw new BaseException("该模型不存在");
        }
        modelL.setIsDisabled(isDisabled);
        boolean isUpdated = this.updateById(modelL);
        if (!isUpdated) {
            throw new BaseException("设置模型是否禁用失败");
        }
        return isUpdated;
    }

}
