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
import com.yunjing.approval.model.vo.ModelItemVO;
import com.yunjing.approval.model.vo.SetConditionVO;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.IConditionService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.service.IProcessService;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author roc
 * @date 2017/12/21
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
    public boolean delete(String modelId, String conditionId) throws Exception {

        processService.delete(modelId, conditionId);
        Wrapper<SetsCondition> wrapper = new EntityWrapper<>();
        wrapper.eq("model_id", modelId);
        if (null == conditionId) {
            wrapper.and("id=''").or("id is null");
        } else {
            wrapper.and("id={0}", conditionId);
        }
        this.delete(wrapper);

        return true;
    }

    @Override
    public List<SetConditionVO> getConditionList(String modelId) throws Exception {
        if (null == modelId) {
            throw new BaseException("模型主键不存在");
        }

        Wrapper<SetsCondition> wrapper = new EntityWrapper<>();
        wrapper.eq("model_id", modelId);
        wrapper.orderBy(true, "enabled", false);
        wrapper.orderBy(true, "sort", true);

        List<SetsCondition> listCondition = this.selectList(wrapper);
        List<SetConditionVO> list = new ArrayList<>();
        List<String> conditionIds = new ArrayList<>();
        for (SetsCondition setsCondition : listCondition) {
            SetConditionVO setConditionVO = new SetConditionVO();
            conditionIds.add(setsCondition.getId());
            List<UserVO> userVoList = processService.getProcess(modelId, conditionIds);
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
    public String getCondition(String modelId, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        List<SetsCondition> list = this.selectList(Condition.create().where("model_id={0}", modelId));

        if (list != null && list.size() > 0) {

            boolean isEquals = false;

            for (SetsCondition condition : list) {
                String cdn = condition.getCdn();
                String[] temp = cdn.split(" ");

                if (cdn.indexOf("=") != -1) {
                    isEquals = true;
                }

                if (isEquals) {
                    if (StringUtils.isNotBlank(temp[2])) {
                        if (value.equals(temp[2])) {
                            return condition.getId();
                        }
                    }
                } else {
                    int length = temp.length;
                    double val = 0;

                    try {
                        val = Double.parseDouble(value);
                    } catch (Exception e) {

                    }

                    if (length == 3) {
                        double num = Double.parseDouble(temp[2]);

                        if ("≤".equals(temp[1])) {
                            if (val <= num) {
                                return condition.getId();
                            }
                        }

                        if ("＞".equals(temp[1])) {
                            if (val > num) {
                                return condition.getId();
                            }
                        }
                    } else if (length == 5) {
                        double num1 = Double.parseDouble(temp[0]);
                        double num2 = Double.parseDouble(temp[4]);

                        if (num1 < val && val <= num2) {
                            return condition.getId();
                        }
                    }
                }
            }
        }
        return null;
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
            throw new BaseException("模型版本异常");
        }
        Wrapper<ModelItem> wrapper = new EntityWrapper<>();
        wrapper.eq("model_id", modelId).eq("data_type", 3).eq("item_version", version);
        wrapper.orderBy(true, "priority", true);
        List<ModelItem> list = modelItemService.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }

        List<ModelItemVO> voList = new ArrayList<>(list.size());
        list.forEach(item -> {
            ModelItemVO vo = new ModelItemVO(item);
            voList.add(vo);
        });

        return voList;
    }

    @Override
    public List<SetConditionVO> save(String modelId, String judge,String memberIds) throws Exception {
        // 解析
        Map<String, String> param = new HashMap<>(1);
        JSONArray jsonArray = JSON.parseArray(judge);
        Iterator<Object> it = jsonArray.iterator();
        while (it.hasNext()) {
            JSONObject obj = (JSONObject) it.next();
            String field = obj.getString("field");
            String value = obj.getString("value");
            param.put(field, value);
        }
        ModelL model = modelService.selectById(modelId);
        Integer version = model.getModelVersion();
        if (version == null || version < 1) {
            throw new BaseException("模型版本异常");
        }
        List<SetsCondition> list = new ArrayList<>(16);
        int i = 0;
        for (Map.Entry<String, String> entry : param.entrySet()) {
            SetsCondition condition = new SetsCondition();
            condition.setId(IDUtils.uuid());
            condition.setModelId(modelId);
            condition.setCdn(entry.getKey() + " = " + entry.getValue());
            condition.setEnabled(1);
            condition.setSort(i + 1);
            list.add(condition);
            // 保存审批人
            processService.updateProcess(modelId, condition.getId(), memberIds);
        }

        return this.getConditionList(modelId);
    }

}