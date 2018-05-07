package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ConditionMapper;
import com.yunjing.approval.model.entity.ModelItem;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.entity.SetsCondition;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.service.IConditionService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.service.IProcessService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.*;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
@Service
public class ConditionServiceImpl extends BaseServiceImpl<ConditionMapper, SetsCondition> implements IConditionService {

    @Autowired
    private IProcessService processService;

    @Autowired
    private IModelService modelService;

    @Autowired
    private IModelItemService modelItemService;

    @Autowired
    private ConditionMapper conditionMapper;
    @Autowired
    private IConditionService cdnService;

    @Override
    public boolean delete(String modelId) throws Exception {
        boolean isDelete = false;
        List<SetsCondition> conditionList = this.selectList(Condition.create().where("model_id={0}", modelId));
        List<String> conditionIds = conditionList.stream().map(SetsCondition::getId).collect(Collectors.toList());
        if (!conditionIds.isEmpty()) {
            for (String conditionId : conditionIds) {
                boolean delete = processService.delete(modelId, conditionId);
                if (!delete) {
                    throw new DeleteMessageFailureException("删除审批流程失败");
                }
            }
            isDelete = this.deleteBatchIds(conditionIds);
            if (!isDelete) {
                throw new DeleteMessageFailureException("删除审批条件失败");
            }
        }
        return isDelete;
    }

    @Override
    public List<SetConditionVO> getConditionList(String modelId) throws Exception {

        List<SetsCondition> listCondition = this.selectList(Condition.create().eq("model_id", modelId).orderBy(true, "enabled", false).orderBy(true, "sort", true));
        List<SetConditionVO> list = new ArrayList<>();
        List<String> conditionIds = new ArrayList<>();
        for (SetsCondition setsCondition : listCondition) {
            SetConditionVO setConditionVO = new SetConditionVO();
            conditionIds.add(setsCondition.getId());
            List<UserVO> userVoList = processService.getProcess(modelId, conditionIds);
            conditionIds.clear();
            setConditionVO.setConditionId(setsCondition.getId());
            setConditionVO.setCdn(setsCondition.getCdn());
            setConditionVO.setContent(setsCondition.getContent());
            setConditionVO.setEnabled(setsCondition.getEnabled());
            setConditionVO.setModelId(setsCondition.getModelId());
            setConditionVO.setSort(setsCondition.getSort());
            setConditionVO.setUserVo(userVoList);
            list.add(setConditionVO);
        }
        return list;
    }

    @Override
    public String getCondition(String modelId, ConditionVO conditionVO) {
        if (conditionVO == null) {
            return null;
        }
        List<SetsCondition> list = this.selectList(Condition.create().where("model_id={0}", modelId));
        if (list != null && CollectionUtils.isNotEmpty(list)) {
            for (SetsCondition condition : list) {
                String cdn = condition.getCdn();
                String[] temp = cdn.split(" ");
                if (ApproConstants.RADIO_TYPE_3 == condition.getType()) {
                    if (StringUtils.isNotBlank(temp[2]) && temp[2].contains(conditionVO.getValue())) {
                        return condition.getId();
                    }
                } else if (ApproConstants.NUMBER_TYPE_2 == condition.getType()) {
                    boolean b = judgeDay(temp, conditionVO);
                    if (b) {
                        return condition.getId();
                    }
                }
            }
        }
        return null;
    }

    private boolean judgeDay(String[] temp, ConditionVO conditionVO) {
        boolean result1 = false;
        boolean result2 = false;
        final String f1 = "<", f2 = "≤", f3 = ">", f4 = "≥", f5 = "=";
        if (temp.length == 5) {
            int a = Integer.valueOf(temp[0]);
            int b = Integer.valueOf(temp[4]);
            String a1 = temp[1];
            String b1 = temp[3];
            switch (a1) {
                case f1:
                    if (a < Integer.parseInt(conditionVO.getValue())) {
                        result1 = true;
                    }
                    break;
                case f2:
                    if (a <= Integer.parseInt(conditionVO.getValue())) {
                        result1 = true;
                    }
                    break;
                default:
                    throw new ParameterErrorException("解析天数出现异常");
            }
            switch (b1) {
                case f1:
                    if (Integer.parseInt(conditionVO.getValue()) < b) {
                        result2 = true;
                    }
                    break;
                case f2:
                    if (Integer.parseInt(conditionVO.getValue()) <= b) {
                        result2 = true;
                    }
                    break;
                default:
                    throw new ParameterErrorException("解析天数出现异常");
            }
            if (result1 && result2) {
                return true;
            } else {
                return false;
            }
        } else if (temp.length == 3) {
            int a = Integer.valueOf(temp[2]);
            String a1 = temp[1];
            switch (a1) {
                case f1:
                    if (conditionVO.getType() == ApproConstants.NUMBER_TYPE_2 && Integer.parseInt(conditionVO.getValue()) < a) {
                        result1 = true;
                    }
                    break;
                case f2:
                    if (conditionVO.getType() == ApproConstants.NUMBER_TYPE_2 && Integer.parseInt(conditionVO.getValue()) <= a) {
                        result1 = true;
                    }
                    break;
                case f3:
                    if (conditionVO.getType() == ApproConstants.NUMBER_TYPE_2 && Integer.parseInt(conditionVO.getValue()) > a) {
                        result1 = true;
                    }
                    break;
                case f4:
                    if (conditionVO.getType() == ApproConstants.NUMBER_TYPE_2 && Integer.parseInt(conditionVO.getValue()) >= a) {
                        result1 = true;
                    }
                    break;
                case f5:
                    if (conditionVO.getType() == ApproConstants.NUMBER_TYPE_2 && Integer.parseInt(conditionVO.getValue()) == a) {
                        result1 = true;
                    }
                    break;
                default:
                    throw new ParameterErrorException("解析天数出现异常");
            }
            return result1;
        } else {
            return false;
        }
    }

    @Override
    public List<SetsCondition> getFirstCondition(String modelId) {
        List<SetsCondition> setsCondition = this.selectList(Condition.create().where("model_id={0}", modelId).and("enabled=1"));
        return setsCondition;
    }

    @Override
    public ConditionAndApproverVO getJudgeList(String modelId) throws Exception {
        ModelL model = modelService.selectById(modelId);
        Integer version = model.getModelVersion();
        if (version == null || version < 1) {
            throw new MessageNotExitException("模型版本异常");
        }
        Wrapper<ModelItem> wrapper = new EntityWrapper<>();
        String dataType = ApproConstants.RADIO_TYPE_3 + "," + ApproConstants.NUMBER_TYPE_2;
        wrapper.eq("model_id", modelId).in("data_type", dataType).eq("item_version", version);
        wrapper.orderBy(true, "priority", true);
        List<ModelItem> list = modelItemService.selectList(wrapper);
        List<SetsCondition> conditionList = conditionMapper.selectConditionList(modelId);
        String value = "值为空";
        String dayNum = "天数为空";
        List<String> conditionIds = new ArrayList<>();
        for (SetsCondition setsCondition : conditionList) {
            int type = setsCondition.getType();
            String condition = setsCondition.getCdn();
            if (ApproConstants.RADIO_TYPE_3 == type) {
                conditionIds.add(setsCondition.getId());
                value = condition.substring(condition.lastIndexOf(" "), condition.length()).trim();
            } else if (ApproConstants.NUMBER_TYPE_2 == type) {
                conditionIds.add(setsCondition.getId());
                dayNum = condition;
            }
        }
        List<ModelItemVO> voList = new ArrayList<>();
        if (list != null && CollectionUtils.isNotEmpty(list)) {
            for (ModelItem item : list) {
                ModelItemVO vo = new ModelItemVO(item);
                vo.setModelItemId(item.getId());
                if(item.getDataType() == ApproConstants.RADIO_TYPE_3){
                    vo.setValue(value);
                }else if(item.getDataType() == ApproConstants.NUMBER_TYPE_2){
                    vo.setDayNum(dayNum);
                }
                voList.add(vo);
            }
        }
        ConditionAndApproverVO result = new ConditionAndApproverVO();
        result.setModelItemList(voList);
        // 获取审批人
        if (CollectionUtils.isNotEmpty(conditionIds)) {
            List<String> ids = conditionIds.stream().distinct().collect(Collectors.toList());
            List<UserVO> approverList = processService.getProcess(modelId, ids);
            // 去重
            List<UserVO> collect = approverList.stream().distinct().collect(Collectors.toList());
            result.setApproverList(collect);
            String cIds = ids.stream().collect(Collectors.joining(","));
            result.setConditionIds(cIds);
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<SetConditionVO> save(String modelId, String judge, String memberIds, String conditionIds) throws Exception {

        // 解析
        JSONArray jsonArray = JSON.parseArray(judge);
        Iterator<Object> it = jsonArray.iterator();
        List<ConditionVO> conditionVOList = new ArrayList<>();
        while (it.hasNext()) {
            JSONObject obj = (JSONObject) it.next();
            ConditionVO conditionVO = JSONObject.parseObject(obj.toJSONString(), ConditionVO.class);
            conditionVOList.add(conditionVO);
        }
        // 先删除旧的审批条件，再设置新的审批条件
        boolean delete = this.delete(Condition.create().where("model_id={0}", modelId));
        if (StringUtils.isNotBlank(conditionIds)) {
            String[] cIds = conditionIds.split(",");
            processService.delete(Condition.create().where("model_id={0}", modelId).in("condition_id", cIds));
        }
        if (delete) {
            int i = 0;
            for (ConditionVO conditionVO : conditionVOList) {
                if (StringUtils.isNotBlank(conditionVO.getValue())) {
                    SetsCondition condition = new SetsCondition();
                    condition.setId(IDUtils.uuid());
                    condition.setModelId(modelId);
                    if (ApproConstants.RADIO_TYPE_3 == conditionVO.getType()) {
                        String cdn = conditionVO.getField() + " = " + conditionVO.getValue();
                        condition.setCdn(cdn);
                        condition.setType(ApproConstants.RADIO_TYPE_3);
                    } else if (ApproConstants.NUMBER_TYPE_2 == conditionVO.getType()) {
                        String cdn = conditionVO.getValue();
                        condition.setCdn(cdn);
                        condition.setType(ApproConstants.NUMBER_TYPE_2);
                    }
                    condition.setEnabled(1);
                    condition.setSort(i + 1);
                    boolean insert = this.insert(condition);
                    if (!insert) {
                        throw new InsertMessageFailureException("保存审批条件失败");
                    }
                    boolean b = processService.updateProcess(modelId, condition.getId(), memberIds);
                    if (!b) {
                        throw new UpdateMessageFailureException("按条件保存审批人失败");
                    }
                    // 修改判断条件
                    ModelItem modelItem = modelItemService.selectById(conditionVO.getModelItemId());
                    if (modelItem != null) {
                        modelItem.setIsJudge(conditionVO.getJudge());
                        boolean isUpdated = modelItemService.updateById(modelItem);
                        if (!isUpdated){
                            throw new UpdateMessageFailureException("修改判断条件失败");
                        }
                    }
                }
            }
        }
        return this.getConditionList(modelId);
    }

}