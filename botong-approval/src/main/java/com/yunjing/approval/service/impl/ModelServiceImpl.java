package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ModelItemMapper;
import com.yunjing.approval.dao.mapper.ModelMapper;
import com.yunjing.approval.model.entity.ModelCategory;
import com.yunjing.approval.model.entity.ModelItem;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.ModelItemVO;
import com.yunjing.approval.model.vo.ModelListVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.service.ICopyService;
import com.yunjing.approval.service.IModelCategoryService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.util.ApproConstants;
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
    @Autowired
    private ModelItemMapper modelItemMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ModelListVO> findModelList(String orgId) {
        List<ModelVO> modelVOList = modelMapper.selectLists(orgId);
        List<ModelItem> modelItems = modelItemMapper.selectAll(orgId);
        for (ModelVO modelVO : modelVOList) {
            // 过滤属于某个审批模板的所有详情项
            List<ModelItem> items = modelItems.stream().filter(modelItem -> modelItem.getModelId().equals(modelVO.getModelId())).collect(Collectors.toList());
             // 过滤出类型是单选框的modelItem
            Set<ModelItem> itemSet = items.stream().filter(modelItem -> modelItem.getDataType().equals(ApproConstants.RADIO_TYPE_3)).collect(Collectors.toSet());
            modelVO.setItemVOSet(itemSet);
            // 过滤出所有需要必填的单选框
            Set<ModelItem> set = itemSet.stream().filter(modelItem -> modelItem.getIsRequired().equals(1)).collect(Collectors.toSet());
            if(!set.isEmpty()){
                // 有必填的单选框则标识true
                modelVO.setHaveRequired(true);
            }else {
                // 没有必填的单选框标识为false
                modelVO.setHaveRequired(false);
            }
        }

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
                    List<ModelVO> modelVOS = modelVOList.stream().filter(modelVO1 -> modelCategory.getId().equals(modelVO1.getCategoryId())).collect(Collectors.toSet()).stream().collect(Collectors.toList());
                    Collections.sort(modelVOS, new Comparator<ModelVO>() {
                        @Override
                        public int compare(ModelVO o1, ModelVO o2) {
                            if (o1.getSort() > o2.getSort()) {
                                return 1;
                            } else if (o1.getSort()<(o2.getSort())) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
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
    public boolean moveModel(String categoryId, String modelId) throws Exception {

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
    public List<ModelVO> findModelListByOrgId(String orgId) {
        List<ModelVO> modelVOList = modelMapper.selectLists(orgId);
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

    @Override
    public List<String> getLogo() throws Exception {
        List<ModelL> list = this.selectList(Condition.create().where("is_def={0}", 1));
        List<String> logo = list.stream().map(ModelL::getLogo).collect(Collectors.toList());
        return logo;
    }

    @Override
    public List<ModelL> findModel(String orgId) {
        return modelMapper.selectModelListByOrgId(orgId);
    }

    @Override
    public boolean deleteModel(String orgId) {
        return modelMapper.deleteModelListByOrgId(orgId);
    }
}
