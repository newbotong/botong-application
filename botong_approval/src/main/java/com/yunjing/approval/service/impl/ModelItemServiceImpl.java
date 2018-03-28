package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ConditionMapper;
import com.yunjing.approval.dao.mapper.ModelItemMapper;
import com.yunjing.approval.dao.mapper.ModelLMapper;
import com.yunjing.approval.model.entity.ModelItem;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.entity.OrgModel;
import com.yunjing.approval.model.vo.ClientModelItemVO;
import com.yunjing.approval.model.vo.ModelItemVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.service.IApprovalSetsService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.service.IOrgModelService;
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
import java.util.List;
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
    private IApprovalSetsService approvalSetsService;

    @Autowired
    private ConditionMapper conditionMapper;

    @Autowired
    private ModelLMapper modelLMapper;

    /**
     * 1-多行输入框 2-数字输入框 3-单选框 4-日期 5-日期区间 6-单行输入框 7-明细 8-说明文字 9-金额 10- 图片 11-附件
     */
    private final int[] types = {1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12};

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ClientModelItemVO getModelItem(Long modelId) throws Exception {
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
                modelItemVO.setModelItems(itemList);
            }
            modelItemVOS.add(modelItemVO);

        }

        modelVO.setModelItems(modelItemVOS);
        ClientModelItemVO clientModelItemVO = new ClientModelItemVO(modelVO);
        clientModelItemVO.setDeptId(6383142972988329992L);
        clientModelItemVO.setDeptName("互联网时代");
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

        vo.setModelItems(items);
        return vo;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ModelVO saveModelItem(Long orgId, Long userId, String json) throws Exception {

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

        List<ModelItemVO> itemVOS = vo.getModelItems();
        if (CollectionUtils.isEmpty(itemVOS)) {
            throw new BaseException("字段数据不存在");
        }

        Long modelId = vo.getModelId();
        int version = 1;

        ModelL entity = new ModelL();
        boolean isNew = false;
        if (null != vo.getModelId()) {
            entity = modelService.selectById(modelId);
            if (entity == null) {
                throw new BaseException("模型信息不存在");
            }
            version = entity.getModelVersion() + 1;
        } else {
            isNew = true;
            modelId = IDUtils.getID();
            entity.setId(modelId);
            entity.setLogo("https://web.botong.tech/resource/img/public.png");

            Integer max = modelLMapper.getMaxSort(orgId);
            if (max == null) {
                max = 0;
            }
            entity.setSort(max + 1);
            entity.setIsDisabled(0);
            entity.setIsDef(0);
            entity.setProvider(userId);
            entity.setModelType(2);
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
                orgModel.setId(IDUtils.getID());
                orgModel.setOrgId(orgId);
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

    /**
     * @param itemVOS 字段视图集合
     * @param modelId 模型主键
     * @param version 模型版本号
     * @param flag    是否为子字段
     * @param parent  父字段视图
     * @return 字段结婚
     * @throws Exception 异常
     */
    private List<ModelItem> getModelItemList(List<ModelItemVO> itemVOS, Long modelId, int version, boolean flag, ModelItem parent) throws Exception {
        List<ModelItem> entityList = new ArrayList<>();
        int i = 1;
        for (ModelItemVO itemVO : itemVOS) {
            int type = itemVO.getType();
            if (!ArrayUtils.contains(types, type)) {
                throw new BaseException("模型数据格式不正确 - 字段类型不正确");
            }

            ModelItem item = new ModelItem();

            Long modelItemId = IDUtils.getID();
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
            Integer isDisplay = itemVO.getIsDisplay();
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
            Integer isRequired = itemVO.getIsRequired();
            if (isRequired != null && isRequired == 1) {
                isRequired = 1;
            } else {
                isRequired = 0;
            }
            item.setIsRequired(isRequired);

            // 是否为判断条件
            if (!flag) {
                if (type == 2 || type == 3) {
                    Integer isJudge = itemVO.getIsJudge();
                    if (isJudge != null && isJudge == 1) {
                        isJudge = 1;
                    } else {
                        isJudge = 0;
                    }
                    item.setIsJudge(isJudge);
                } else {
                    item.setIsJudge(0);
                }
            } else {
                item.setIsJudge(0);
            }

            // 版本
            item.setItemVersion(version);

            // 子字段
            if (flag) {
                item.setIsChild(parent.getId());
            }
            entityList.add(item);

            // 明细
            List<ModelItemVO> child = itemVO.getChild();
            if (!flag && type == 7 && CollectionUtils.isNotEmpty(child)) {
                List<ModelItem> childList = this.getModelItemList(child, modelId, version, true, item);
                if (CollectionUtils.isNotEmpty(childList)) {
                    entityList.addAll(childList);
                } else {
                    throw new BaseException("字段子集数据解析异常");
                }
            }

            i++;
        }

        return entityList;
    }

}
