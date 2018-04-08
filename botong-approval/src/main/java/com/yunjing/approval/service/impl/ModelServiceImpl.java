package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ModelMapper;
import com.yunjing.approval.model.entity.ModelCategory;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.ModelListVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.service.ICopyService;
import com.yunjing.approval.service.IModelCategoryService;
import com.yunjing.approval.service.IModelService;
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
public class ModelServiceImpl extends BaseServiceImpl<ModelMapper, ModelL> implements IModelService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ICopyService copyService;

    @Autowired
    private IModelCategoryService modelCategoryService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ModelListVO> findModelList(Long orgId) {
        List<ModelVO> modelVOList = modelMapper.selectModelListByOrgId(orgId);
        List<ModelListVO> modelListVOList = new ArrayList<>();
        List<ModelCategory> list = modelCategoryService.selectList(Condition.create().where("org_id={0}", orgId));
        if (!list.isEmpty()) {
            for (ModelCategory modelCategory : list) {
                ModelListVO modelListVO = new ModelListVO();
                modelListVO.setCategoryId(modelCategory.getId());
                modelListVO.setCategroyName(modelCategory.getCategoryName());
                modelListVO.setSort(modelCategory.getSort());
                modelListVO.setUpdateTime(modelCategory.getUpdateTime());
                if (modelCategory.getId() != null) {
                    List<ModelVO> modelVOS = modelVOList.stream().filter(modelVO1 -> modelCategory.getId().equals(modelVO1.getCategoryId())).collect(Collectors.toList());
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
    public boolean sortedModel(Long categoryId, String sortArray) throws Exception {
        boolean isUpdated = false;
        Map<Long, Integer> modelSortMap = null;
        try {
            // 解析排序数据
            JSONArray sortJSONArray = JSONArray.parseArray(sortArray);
            // 封装排序集合为KEY-VALUE以部门编号为KEY，位置为VALUE
            modelSortMap = new HashMap<>(sortJSONArray.size());
            for (int i = 0; i < sortJSONArray.size(); i++) {
                JSONObject sortJSON = sortJSONArray.getJSONObject(i);
                modelSortMap.put(sortJSON.getLong("modelId"), sortJSON.getInteger("sort"));
            }
        } catch (Exception e) {
            throw new BaseException("解析分组排序数据错误");
        }

        // 查询当前企业的所有分组
        List<ModelL> modelList = this.selectList(Condition.create().where("category_id = {0}", categoryId));
        if (modelList != null && !modelList.isEmpty()) {
            for (ModelL model : modelList) {
                Integer sort = modelSortMap.get(model.getId());
                model.setSort(sort);
            }
            isUpdated = this.updateBatchById(modelList);
            if (isUpdated == false) {
                throw new UpdateMessageFailureException("更新部门排序信息失败");
            }
        } else {
            throw new MessageNotExitException("当前分组下不存在审批模版");
        }

        return isUpdated;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean moveModel(Long categoryId, Long modelId) throws Exception {

        ModelL modelL = this.selectById(modelId);
        if (modelL == null) {
            throw new MessageNotExitException("模型不存在");
        }
        modelL.setCategoryId(categoryId);
        boolean updated = this.update(modelL, Condition.create().where("id={0}", modelId));
        return updated;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ModelVO> findModelListByOrgId(Long orgId) {
        List<ModelVO> modelVOList = modelMapper.selectModelListByOrgId(orgId);
        for (ModelVO modelVO : modelVOList) {
            // 获取每个审批项的抄送人数量
            int count = copyService.selectCount(Condition.create().where("model_id={0}", modelVO.getModelId()));
            modelVO.setCopyUserCount(count);
        }
        return modelVOList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateVisibleRange(Long modelId, String deptIds, String userIds) throws Exception {
        // TODO 调用rpc接口处理可见范围权限业务

        ModelL modelL = this.selectById(modelId);

        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateIsDisabled(Long modelId, Integer isDisabled) throws Exception {
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
