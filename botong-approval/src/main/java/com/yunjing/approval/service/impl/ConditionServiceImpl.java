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
import com.yunjing.approval.model.vo.ConditionVO;
import com.yunjing.approval.model.vo.ModelItemVO;
import com.yunjing.approval.model.vo.SetConditionVO;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.IConditionService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.service.IProcessService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.DeleteMessageFailureException;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.global.exception.MessageNotExitException;
import com.yunjing.mommon.global.exception.ParameterErrorException;
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
import java.util.regex.Pattern;
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
        final String f1 = "＜", f2 = "≤", f3 = "＞", f4 = "≥", f5 = "=";
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
                case f3:
                    if (b > Integer.parseInt(conditionVO.getValue())) {
                        result2 = true;
                    }
                    break;
                case f4:
                    if (b >= Integer.parseInt(conditionVO.getValue())) {
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
                    if (Integer.parseInt(conditionVO.getValue()) < a) {
                        result1 = true;
                    }
                    break;
                case f2:
                    if (Integer.parseInt(conditionVO.getValue()) <= a) {
                        result1 = true;
                    }
                    break;
                case f3:
                    if (Integer.parseInt(conditionVO.getValue()) > a) {
                        result1 = true;
                    }
                    break;
                case f4:
                    if (Integer.parseInt(conditionVO.getValue()) >= a) {
                        result1 = true;
                    }
                    break;
                case f5:
                    if (Integer.parseInt(conditionVO.getValue()) == a) {
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
    public List<ModelItemVO> getJudgeList(String modelId) throws Exception {
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
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        List<SetsCondition> conditionList = conditionMapper.selectList(Condition.create().where("model_id={0}", modelId).and("enabled=1"));
        String value = "danxuankuang";
        String dayNum = "tianshu";
        for (SetsCondition setsCondition : conditionList) {
            int type = setsCondition.getType();
            String condition = setsCondition.getCdn();
            if (ApproConstants.RADIO_TYPE_3 == type) {
                value = condition.substring(condition.lastIndexOf(" "), condition.length()).trim();
            } else if (ApproConstants.NUMBER_TYPE_2 == type) {
                dayNum = condition;
            }
        }
        List<ModelItemVO> voList = new ArrayList<>(list.size());
        for (ModelItem item : list) {
            ModelItemVO vo = new ModelItemVO(item);
            vo.setValue(value);
            vo.setDayNum(dayNum);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<SetConditionVO> save(String modelId, String judge, String memberIds) throws Exception {
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
        if (delete) {
            int i = 0;
            for (ConditionVO conditionVO : conditionVOList) {
                SetsCondition condition = new SetsCondition();
                condition.setId(IDUtils.uuid());
                condition.setModelId(modelId);
                if (ApproConstants.RADIO_TYPE_3 == conditionVO.getType()) {
                    condition.setCdn(conditionVO.getField() + " = " + conditionVO.getValue());
                    condition.setType(ApproConstants.RADIO_TYPE_3);
                } else if (ApproConstants.NUMBER_TYPE_2 == conditionVO.getType()) {
                    condition.setCdn(conditionVO.getValue());
                    condition.setType(ApproConstants.NUMBER_TYPE_2);
                }
                condition.setEnabled(1);
                condition.setSort(i + 1);
                boolean insert = this.insert(condition);
                if (!insert) {
                    throw new InsertMessageFailureException("保存审批条件失败");
                }
                // 保存审批人
                boolean b = processService.updateProcess(modelId, condition.getId(), memberIds);
            }
        }
        return this.getConditionList(modelId);
    }

}