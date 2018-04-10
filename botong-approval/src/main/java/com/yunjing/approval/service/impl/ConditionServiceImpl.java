package com.yunjing.approval.service.impl;

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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        for (SetsCondition setsCondition : listCondition) {
            SetConditionVO setConditionVO = new SetConditionVO();
            List<UserVO> userVoList = processService.getProcess(modelId, setsCondition.getId());
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
    public SetsCondition getFirstCondition(String modelId) {
        return this.selectOne(Condition.create().where("model_id={0}", modelId).and("enabled=1").and("sort=1"));
    }

    @Override
    public List<ModelItemVO> getJudgeList(String modelId) throws Exception {
        ModelL model = modelService.selectById(modelId);
        if (model == null) {
            throw new BaseException("模型不存");
        }

        Integer version = model.getModelVersion();
        if (version == null || version < 1) {
            throw new BaseException("模型版本异常");
        }

        Wrapper<ModelItem> wrapper = new EntityWrapper<>();
        wrapper.eq("model_id", modelId).eq("is_judge", 1).eq("item_version", version);
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
    public List<SetConditionVO> save(String modelId, String field, String numbers) throws Exception {

        if (StringUtils.isBlank(field)) {
            throw new BaseException("字段不存在");
        }

        ModelL model = modelService.selectById(modelId);
        if (model == null) {
            throw new BaseException("模型不存在");
        }

        Integer version = model.getModelVersion();
        if (version == null || version < 1) {
            throw new BaseException("模型版本异常");
        }


        Wrapper<ModelItem> wrapper = new EntityWrapper<>();
        wrapper.eq("model_id", modelId).eq("field", field).eq("item_version", version);

        ModelItem item = modelItemService.selectOne(wrapper);

        if (item == null || null == item.getId()) {
            throw new BaseException("字段类型错误");
        }

        List<SetsCondition> list = new ArrayList<>(16);

        String label = item.getItemLabel();

        int type = item.getDataType();

        // 数字输入框
        final int dataTypeNum = 2;

        // 单选框
        final int dataTypeRadio = 3;

        if (type == dataTypeRadio) {
            if (StringUtils.isBlank(item.getOptValue())) {
                throw new BaseException("字段无可选项");
            }

            String[] opts = StringUtils.split(item.getOptValue(), ",");

            if (ArrayUtils.isEmpty(opts)) {
                throw new BaseException("字段无可选项");
            }

            if (StringUtils.isBlank(label)) {
                throw new BaseException("字段名称为空");
            }

            String flag = " = ";

            for (int i = 0; i < opts.length; i++) {
                String opt = opts[i];

                if (StringUtils.isBlank(opt)) {
                    continue;
                }

                SetsCondition entity = new SetsCondition();
                entity.setId(IDUtils.uuid());
                entity.setModelId(modelId);

                entity.setContent(label + flag + opt);
                entity.setCdn(field + flag + opt);

                entity.setEnabled(1);
                entity.setSort(i + 1);

                list.add(entity);
            }
        }

        if (type == dataTypeNum) {
            if (StringUtils.isBlank(numbers)) {
                throw new BaseException("字段参数值不正确");
            }

            String[] vals = StringUtils.split(numbers, ",");
            if (ArrayUtils.isEmpty(vals)) {
                throw new BaseException("字段参数值不正确");
            }

            Integer num = 0;

            String flag = " ≤ ";

            for (int i = 0; i < vals.length; i++) {
                String value = vals[i];

                if (StringUtils.isBlank(value)) {
                    continue;
                }

                String content;
                String cdn;

                if (num > 0) {
                    content = num + " ＜ " + label + flag + value;
                    cdn = num + " ＜ " + label + flag + value;
                } else {
                    content = label + flag + value;
                    cdn = field + flag + value;
                }

                SetsCondition entity = new SetsCondition();
                entity.setId(IDUtils.uuid());
                entity.setModelId(modelId);

                entity.setContent(content);
                entity.setCdn(cdn);
                entity.setEnabled(1);
                entity.setSort(i + 1);

                list.add(entity);

                num = Integer.parseInt(value);

                if (i == vals.length - 1) {
                    SetsCondition e = new SetsCondition();
                    e.setId(IDUtils.uuid());
                    e.setModelId(modelId);

                    flag = " ＞ ";

                    content = label + flag + value;
                    cdn = field + flag + value;

                    e.setContent(content);
                    e.setCdn(cdn);
                    e.setEnabled(1);
                    e.setSort(i + 2);

                    list.add(e);
                }
            }
        }

        conditionMapper.disableByModelId(modelId);
        if (!list.isEmpty()) {
            this.insertBatch(list);
        }

        return this.getConditionList(modelId);
    }
}