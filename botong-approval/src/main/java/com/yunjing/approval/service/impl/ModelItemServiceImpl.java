package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ConditionMapper;
import com.yunjing.approval.dao.mapper.ModelItemMapper;
import com.yunjing.approval.dao.mapper.ModelMapper;
import com.yunjing.approval.model.entity.ModelItem;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.entity.OrgModel;
import com.yunjing.approval.model.entity.SetsCondition;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;


/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
@Service
public class ModelItemServiceImpl extends BaseServiceImpl<ModelItemMapper, ModelItem> implements IModelItemService {

    @Autowired
    private IModelService modelService;

    @Autowired
    private IOrgModelService orgModelService;

    @Autowired
    private ConditionMapper conditionMapper;
    @Autowired
    private IConditionService conditionService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IApprovalSetsService approvalSetsService;

    @Autowired
    private IProcessService processService;

    @Autowired
    private ICopyService copyService;
    @Autowired
    private ModelItemMapper modelItemMapper;
    @Autowired
    private IModelCategoryService modelCategoryService;

    /**
     * 1-多行输入框 2-数字输入框 3-单选框 4-日期 5-日期区间 6-单行输入框 7-明细 8-说明文字 9-金额 10- 图片 11-附件
     */
    private final int[] types = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ClientModelItemVO getModelItem(String modelId) throws Exception {
        ModelL modelL = modelService.selectById(modelId);
        List<ModelItem> itemList = this.selectList(Condition.create().where("model_id={0}", modelId).and("item_version={0}", modelL.getModelVersion()).orderBy("priority"));
        ModelVO modelVO = new ModelVO();
        modelVO.setModelId(modelId);
        modelVO.setModelName(modelL.getModelName());
        List<ModelItemVO> modelItemVOS = new ArrayList<>();
        for (ModelItem modelItem : itemList) {
            if (null != modelItem.getIsChild()) {
                continue;
            }
            if (modelItem.getIsDisplay() != 1) {
                continue;
            }
            ModelItemVO modelItemVO = new ModelItemVO(modelItem);
            if (modelItem.getDataType() == 7) {
                modelItemVO.setModelItemId(modelItem.getId());
                modelItemVO.setLabel(modelItem.getItemLabel());
                modelItemVO.setLabels(modelItem.getItemLabels());
                modelItemVO.setField(modelItem.getField());
                modelItemVO.setDisplay(modelItem.getIsDisplay());
                modelItemVO.setRequired(modelItem.getIsRequired());
                modelItemVO.setHelp(modelItem.getHelp());
                List<ModelItem> modelItems = this.selectList(Condition.create().where("is_child={0}", modelItem.getId()).orderBy("priority"));
                modelItemVO.setItems(modelItems);
            }
            modelItemVOS.add(modelItemVO);

        }

        modelVO.setItems(modelItemVOS);
        ClientModelItemVO clientModelItemVO = new ClientModelItemVO(modelVO);

        // 获取审批流程设置类型，set=0:不分条件设置审批人 set=1:分条件设置审批人
        ApprovalSetVO approvalSet = approvalSetsService.getApprovalSet(modelId);
        clientModelItemVO.setSet(approvalSet.getSetting());

        // 获取预设人员筛选字段
        Set<String> keys = new HashSet<>();
        if (StringUtils.isNotBlank(modelId)) {
            List<SetsCondition> first = conditionService.getFirstCondition(modelId);
            for (SetsCondition setsCondition : first) {
                if (setsCondition != null) {
                    String cdn = setsCondition.getCdn();
                    if (StringUtils.isNotBlank(cdn)) {
                        String key = cdn.substring(0, cdn.indexOf(" "));
                        keys.add(key);
                    }
                }
            }
        }
        clientModelItemVO.setField(keys);
        // 获取默认审批人
        List<UserVO> processUser = processService.getProcess(modelId, null);
        clientModelItemVO.setApproverVOS(processUser);

        // 获取默认抄送人
        List<UserVO> userVOList = copyService.get(modelId);
        clientModelItemVO.setCopyerVOS(new ArrayList<>());

        return clientModelItemVO;

    }

    private ModelVO getModelVO(ModelL modelL, List<ModelItem> itemList) {
        ModelVO vo = new ModelVO(modelL);
        List<ModelItemVO> items = new ArrayList<>(itemList.size());
        itemList.forEach(item -> {
            if (null == item.getIsChild()) {
                ModelItemVO itemVO = new ModelItemVO(item);
                List<ModelItem> childList = itemList.parallelStream().filter(modelItem -> item.getId().equals(modelItem.getIsChild())).sorted(comparing(ModelItem::getPriority)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(childList)) {
                    List<ModelItemVO> child = new ArrayList<>(childList.size());
                    childList.forEach(c -> child.add(new ModelItemVO(c)));
                    itemVO.setChild(child);
                }
                items.add(itemVO);
            }
        });

        vo.setItems(items);
        return vo;
    }

    @Override
    public ModelVO get(String modelId) throws Exception {

        ModelL modelL = modelService.selectById(modelId);
        if (modelL == null) {
            throw new BaseException("模型信息不存在");
        }

        Wrapper<ModelItem> wrapper = new EntityWrapper<>();
        wrapper.eq("model_id", modelId).eq("item_version", modelL.getModelVersion()).orderBy("priority");
        List<ModelItem> itemList = this.selectList(wrapper);

        if (CollectionUtils.isEmpty(itemList)) {
            throw new BaseException("字段信息不存在");
        }

        return this.getModelVO(modelL, itemList);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ModelVO saveModelItem(String companyId, String memberId, String categoryId, String json) throws Exception {
        if (StringUtils.isBlank(json)) {
            throw new BaseException("模型数据不存在");
        }
        System.out.println(json);

        ModelVO vo;
        try {
            vo = JSONObject.parseObject(json, ModelVO.class);
        } catch (Exception ex) {
            throw new BaseException("模型数据格式不正确");
        }

        if (StringUtils.isBlank(vo.getModelName())) {
            throw new BaseException("审批名称不存在");
        }

        List<ModelItemVO> itemVOS = vo.getItems();
        if (CollectionUtils.isEmpty(itemVOS)) {
            throw new BaseException("字段数据不存在");
        }

        String modelId = vo.getModelId();
        int version = 1;

        ModelL entity = new ModelL();
        boolean isNew = false;
        if (StringUtils.isNotBlank(vo.getModelId())) {
            entity = modelService.selectById(modelId);
            if (entity == null) {
                throw new BaseException("模型信息不存在");
            }
            version = entity.getModelVersion() + 1;
        } else {
            isNew = true;
            modelId = IDUtils.uuid();
            entity.setId(modelId);
            entity.setLogo(vo.getLogo());

            Integer max = modelMapper.getMaxSort(companyId);
            if (max == null) {
                max = 0;
            }
            entity.setSort(max + 1);
            entity.setIsDisabled(0);
            entity.setIsDef(0);
            entity.setProvider(memberId);
            entity.setModelType(2);
            entity.setCategoryId(categoryId);
            entity.setVisibleRange("全部可见");
            vo.setModelId(modelId);
        }
        entity.setModelName(vo.getModelName());
        entity.setModelVersion(version);
        String introduce = vo.getIntroduce();
        if (StringUtils.isNotBlank(introduce)) {
            entity.setIntroduce(vo.getIntroduce());
        }

        List<ModelItem> entityList = this.getModelItemList(itemVOS, modelId, version, false, null);
        if (CollectionUtils.isEmpty(entityList)) {
            throw new BaseException("字段数据解析异常");
        }

        boolean result = modelService.insertOrUpdate(entity);
        if (result) {
            result = this.insertBatch(entityList);
            if (!result) {
                throw new BaseException("字段插入失败");
            }

            if (isNew) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                OrgModel orgModel = new OrgModel();
                orgModel.setId(IDUtils.uuid());
                orgModel.setOrgId(companyId);
                orgModel.setModelId(entity.getId());
                orgModel.setDataType(2);
                orgModel.setCreateTime(now.getTime());
                result = orgModelService.insert(orgModel);
                if (!result) {
                    throw new BaseException("模型关系插入失败");
                }
            } else {
                approvalSetsService.deleteById(modelId);
                conditionMapper.disableByModelId(modelId);
            }
        } else {
            throw new BaseException("模型插入或更新失败");
        }

        return this.getModelVO(entity, entityList);
    }

    @Override
    public boolean deleteModelItemListByOrgId(String orgId) {
        return modelItemMapper.deleteModelItemListByOrgId(orgId);

    }

    /**
     * @param itemVOS 字段视图集合
     * @param modelId 模型主键
     * @param version 模型版本号
     * @param flag    是否为子字段
     * @param parent  父字段视图
     * @return
     * @throws Exception 异常
     */
    private List<ModelItem> getModelItemList(List<ModelItemVO> itemVOS, String modelId, int version, boolean flag, ModelItem parent) throws Exception {
        List<ModelItem> entityList = new ArrayList<>();
        int i = 1;
        for (ModelItemVO itemVO : itemVOS) {
            int type = itemVO.getType();
            if (!ArrayUtils.contains(types, type)) {
                throw new BaseException("模型数据格式不正确 - 字段类型不正确");
            }

            ModelItem item = new ModelItem();

            String modelItemId = IDUtils.uuid();
            item.setId(modelItemId);
            item.setModelId(modelId);

            // 字段
            String field = "field" + i;
            if (flag) {
                field = parent.getField() + "-" + i;
            }
            item.setField(field);

            // 类型
            item.setDataType(type);

            // 标题
            String label = itemVO.getLabel();
            if (itemVO.getType() == 8) {
                label = "说明";
            } else {
                if (StringUtils.isBlank(label)) {
                    throw new BaseException("模型数据格式不正确 - 标题为空");
                }
            }
            item.setItemLabel(label);

            // 排序顺序
            item.setPriority(i);

            // 可选项
            String option = itemVO.getOption();

            if (type == 3 || type == 5) {
                if (StringUtils.isBlank(option)) {
                    throw new BaseException("模型数据格式不正确 - 选项为空");
                }
                item.setOptValue(option);
            }

            // 提示文字
            String help = itemVO.getHelp();
            if (StringUtils.isNotBlank(help)) {
                item.setHelp(help);
            }

            // 单位
            String unit = itemVO.getUnit();
            if (type == 2 && StringUtils.isNotBlank(unit)) {
                item.setUnit(unit);
            }

            // 日期格式
            String dateFormat = itemVO.getFormat();
            if (type == 4 || type == 5) {
                if (StringUtils.isNotBlank(dateFormat)) {
                    if (!"yyyy-MM-dd".equals(dateFormat) && !"yyyy-MM-dd HH:mm".equals(dateFormat)) {
                        throw new BaseException("模型数据格式不正确 - 日期类型不正确");
                    } else {
                        item.setDateFormat(dateFormat);
                    }
                } else {
                    throw new BaseException("模型数据格式不正确 - 日期类型为空");
                }
            }

            // 是否自定义
            item.setIsCustom(1);

            // 是否显示 0:不显示 1:显示
            Integer isDisplay = itemVO.getDisplay();
            if (isDisplay == null) {
                isDisplay = 1;
            } else {
                if (type == 8 && isDisplay != 1) {
                    isDisplay = 0;
                } else {
                    isDisplay = 1;
                }
            }
            item.setIsDisplay(isDisplay);

            // 是否必填
            Integer isRequired = itemVO.getRequired();
            if (isRequired != null && isRequired == 1) {
                isRequired = 1;
            } else {
                isRequired = 0;
            }
            item.setIsRequired(isRequired);

            // 获取单选框中排序最小值
            Integer minSort = modelItemMapper.getMinSort(modelId, ApproConstants.RADIO_TYPE_3);
            // 是否为判断条件
            if (!flag) {
                if (type == 2 || type == 3) {
                    Integer isJudge = itemVO.getJudge();
                    if (isJudge != null && isJudge == 1) {
                        isJudge = 1;
                    } else {
                        isJudge = 0;
                    }
                    item.setIsJudge(isJudge);
                } else {
                    if(item.getPriority().equals(minSort)){
                        item.setIsJudge(1);
                    }else {
                        item.setIsJudge(0);
                    }
                }
            } else {
                item.setIsJudge(0);
            }

            // 版本
            item.setItemVersion(version);

            // 子字段
            if (flag) {
                item.setIsChild(parent.getId().toString());
            }
            entityList.add(item);
            // 明细
            List<ModelItemVO> child = itemVO.getChild();
            if (!flag && type == 7 && CollectionUtils.isNotEmpty(child)) {
                List<ModelItem> childList = this.getModelItemList(child, modelId, version, true, item);
                if (CollectionUtils.isNotEmpty(childList)) {
                    entityList.addAll(childList);
                    childList.clear();
                } else {
                    throw new BaseException("字段子集数据解析异常");
                }
            }
            i++;
        }

        return entityList;
    }
}
