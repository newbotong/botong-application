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
import com.yunjing.approval.model.entity.SetsProcess;
import com.yunjing.approval.model.vo.ModelListVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.service.IModelCategoryService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.service.IProcessService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.MessageNotExitException;
import com.yunjing.mommon.global.exception.MissingRequireFieldException;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.global.exception.UpdateMessageFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.comparingLong;


/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
@Service
public class ModelServiceImpl extends BaseServiceImpl<ModelMapper, ModelL> implements IModelService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IModelCategoryService modelCategoryService;
    @Autowired
    private ModelItemMapper modelItemMapper;
    @Autowired
    private IProcessService processService;

    @Override
    public List<ModelListVO> findModelList(String orgId) {
        List<ModelItem> modelItems = modelItemMapper.selectAll(orgId);
        List<ModelVO> modelVOList = modelMapper.selectLists(orgId);
        List<SetsProcess> setsProcessList = processService.selectList(Condition.EMPTY);
        List<String> modelIds = setsProcessList.parallelStream().map(SetsProcess::getModelId).distinct().collect(Collectors.toList());
        for (ModelVO modelVO : modelVOList) {
            // 过滤属于某个审批模板的所有详情项
            List<ModelItem> items = modelItems.stream().filter(modelItem -> modelItem.getModelId().equals(modelVO.getModelId())).collect(Collectors.toList());
            // 过滤出类型是单选框或数字框的modelItem
            Set<ModelItem> itemSet = items.stream().filter(modelItem -> modelItem.getDataType().equals(ApproConstants.RADIO_TYPE_3) || modelItem.getDataType().equals(ApproConstants.NUMBER_TYPE_2))
                    .collect(Collectors.toSet());
            // 过滤出所有需要必填的单选框或数字框
            Set<ModelItem> set = itemSet.stream().filter(modelItem -> modelItem.getIsRequired().equals(1)).collect(Collectors.toSet());
            if (!set.isEmpty()) {
                // 有必填的单选框或数字框则标识true
                modelVO.setHaveRequired(true);
            } else {
                // 没有必填的单选框或数字框标识为false
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
                    // 判断管理端是否已设置审批人
                    List<ModelVO> sortedModelVOS = modelVOS.stream().sorted(comparingInt(ModelVO::getSort)).map(modelVO -> {
                        boolean isMatch = modelIds.parallelStream().anyMatch(modelId -> modelId.equals(modelVO.getModelId()));
                        modelVO.setIsSetApprover(isMatch);
                        return modelVO;
                    }).collect(Collectors.toList());
                    modelListVO.setModelVOList(sortedModelVOS);
                    modelListVO.setModelCount(sortedModelVOS.size());

                }
                modelListVOList.add(modelListVO);
            }
        }

        // 排序
        List<ModelListVO> sortedModelListVOList = modelListVOList.stream()
                .sorted(comparingInt(ModelListVO::getSort).thenComparing(comparingLong(ModelListVO::getUpdateTime).reversed()))
                .collect(Collectors.toList());
        return sortedModelListVOList;
    }

    @Override
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
            throw new MissingRequireFieldException("解析分组排序数据错误");
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
    public List<ModelVO> findModelListByOrgId(String orgId) {
        List<ModelVO> modelVOList = modelMapper.selectLists(orgId);
        return modelVOList;
    }

    @Override
    public boolean updateVisibleRange(String modelId, String deptIds, String userIds) throws Exception {
        // TODO 调用rpc接口处理可见范围权限业务
        ModelL modelL = this.selectById(modelId);
        return true;
    }

    @Override
    public boolean updateIsDisabled(String modelId, Integer isDisabled) throws Exception {
        ModelL modelL = this.selectById(modelId);
        if (modelL == null) {
            throw new ParameterErrorException("该模型不存在");
        }
        modelL.setIsDisabled(isDisabled);
        boolean isUpdated = this.updateById(modelL);
        if (!isUpdated) {
            throw new UpdateMessageFailureException("设置模型是否禁用失败");
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
