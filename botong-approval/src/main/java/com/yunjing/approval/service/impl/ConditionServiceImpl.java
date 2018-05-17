package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ConditionMapper;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.*;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private IApprovalUserService approvalUserService;

    @Override
    public boolean deleteProcess(String modelId, String conditionId) {
        boolean isDelete = false;
        if (StringUtils.isNotBlank(conditionId)) {
            boolean isDeleted = processService.delete(Condition.create().where("model_id={0}", modelId).and("condition_id={0}", conditionId));
            if (isDeleted) {
                isDelete = this.deleteById(conditionId);
            }
        }
        return isDelete;
    }

    @Override
    public List<SetConditionVO> getConditionList(String modelId) {

        List<SetsCondition> listCondition = this.selectList(Condition.create().eq("model_id", modelId).orderBy(true, "enabled", false).orderBy(true, "sort", true));
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
    public String getCondition(String modelId, List<ConditionVO> conditionVOS) {
        if (conditionVOS == null || CollectionUtils.isEmpty(conditionVOS)) {
            return null;
        }
        String conditionId = "";
        List<SetsCondition> list = this.selectList(Condition.create().where("model_id={0}", modelId).and("enabled=1").orderBy("sort", false));
        if (list != null && CollectionUtils.isNotEmpty(list)) {
            for (SetsCondition condition : list) {
                String cdn = condition.getCdn();
                if (cdn.contains("|")) {
                    String[] t = cdn.split("\\|");
                    boolean flag1 = false;
                    boolean flag2 = false;
                    for (int i = 0; i < t.length; i++) {
                        for (ConditionVO conditionVO : conditionVOS) {
                            String[] temp = t[i].split(" ");
                            if (ApproConstants.RADIO_AND_NUMBER_TYPE_23 == condition.getType() && ApproConstants.RADIO_TYPE_3 == conditionVO.getType() && temp[0].equals(conditionVO.getField())) {
                                flag1 = false;
                                if (StringUtils.isNotBlank(temp[2]) && temp[2].contains(conditionVO.getValue())) {
                                    flag1 = true;
                                }
                            } else if (ApproConstants.RADIO_AND_NUMBER_TYPE_23 == condition.getType() && ApproConstants.NUMBER_TYPE_2 == conditionVO.getType()) {
                                flag2 = judgeDay(temp, conditionVO);
                            }
                        }
                    }
                    if (flag1 && flag2) {
                        return condition.getId();
                    }
                } else {
                    String[] temp = cdn.split(" ");
                    for (ConditionVO conditionVO : conditionVOS) {
                        if (ApproConstants.RADIO_TYPE_3 == condition.getType() && ApproConstants.RADIO_TYPE_3 == conditionVO.getType()) {
                            if (StringUtils.isNotBlank(temp[2]) && temp[2].contains(conditionVO.getValue())) {
                                return condition.getId();
                            }
                        } else if (ApproConstants.NUMBER_TYPE_2 == condition.getType() && ApproConstants.NUMBER_TYPE_2 == conditionVO.getType()) {
                            boolean b = judgeDay(temp, conditionVO);
                            if (b) {
                                return condition.getId();
                            }
                        }
                    }
                }
                // 通过优先级匹配，优先级跟sort排序成正比，如果匹配上了就跳出，如果没有匹配上就进行下一次循序继续匹配
                if (StringUtils.isBlank(conditionId)) {
                    continue;
                } else {
                    break;
                }

            }
        }
        return null;
    }

    private boolean judgeDay(String[] temp, ConditionVO conditionVO) {
        boolean result1 = false;
        boolean result2 = false;
        final String f1 = "<", f2 = "≤", f3 = ">", f4 = "≥", f5 = "=";
        if (temp.length == 5 && temp[2].equals(conditionVO.getField())) {
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
        } else if (temp.length == 3 && temp[0].equals(conditionVO.getField())) {
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

    /**
     * 方法二：推荐，速度最快
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    @Override
    public List<SetsCondition> getFirstCondition(String modelId) {
        List<SetsCondition> setsCondition = this.selectList(Condition.create().where("model_id={0}", modelId).and("enabled=1"));
        return setsCondition;
    }

    @Override
    public List<ModelItemVO> getJudgeList(String modelId) {
        ModelL model = modelService.selectById(modelId);
        Integer version = model.getModelVersion();
        String dataType = ApproConstants.RADIO_TYPE_3 + "," + ApproConstants.NUMBER_TYPE_2;
        List<ModelItem> list = modelItemService.selectList(Condition.create().where("model_id={0}", modelId)
                .and("item_version={0}", version).in("data_type", dataType).orderBy("priority", true));
        List<ModelItemVO> voList = new ArrayList<>();
        if (list != null && CollectionUtils.isNotEmpty(list)) {
            for (ModelItem item : list) {
                voList.add(new ModelItemVO(item));
            }
        }
        return voList;
    }

    @Override
    public ConditionAndApproverVO getConditionAndApprover(String modelId, String conditionId) {
        ModelL model = modelService.selectById(modelId);
        Integer version = model.getModelVersion();
        String dataType = ApproConstants.RADIO_TYPE_3 + "," + ApproConstants.NUMBER_TYPE_2;
        List<ModelItem> list = modelItemService.selectList(Condition.create().where("model_id={0}", modelId)
                .and("item_version={0}", version).in("data_type", dataType).orderBy("priority", true));
        SetsCondition setsCondition = this.selectById(conditionId);
        String value = "值为空";
        String dayNum = "天数为空";
        int type = OptionalInt.of(setsCondition.getType()).orElse(0);
        String condition = Optional.ofNullable(setsCondition.getCdn()).orElse("");
        if (ApproConstants.RADIO_TYPE_3 == type) {
            value = condition.substring(condition.lastIndexOf(" "), condition.length()).trim();
        } else if (ApproConstants.NUMBER_TYPE_2 == type) {
            dayNum = condition;
        } else if (type == ApproConstants.RADIO_AND_NUMBER_TYPE_23) {
            String[] split = condition.split("\\|");
            value = split[0].substring(split[0].lastIndexOf(" "), split[0].length()).trim();
            dayNum = split[1];
        }
        List<ModelItemVO> voList = new ArrayList<>();
        if (list != null && CollectionUtils.isNotEmpty(list)) {
            for (ModelItem item : list) {
                ModelItemVO vo = new ModelItemVO(item);
                vo.setModelItemId(item.getId());
                if (item.getDataType() == ApproConstants.RADIO_TYPE_3) {
                    vo.setValue(value);
                } else if (item.getDataType() == ApproConstants.NUMBER_TYPE_2) {
                    vo.setDayNum(dayNum);
                }
                voList.add(vo);
            }
        }
        ConditionAndApproverVO result = new ConditionAndApproverVO();
        result.setModelItemList(voList);
        // 获取审批人
        List<UserVO> approverList = processService.getProcess(modelId, setsCondition.getId());
        // 去重
        List<UserVO> collect = approverList.stream().distinct().collect(Collectors.toList());
        result.setApproverList(collect);
        result.setConditionId(conditionId);
        return result;
    }

    @Override
    public List<ConditionAndApproverVO> getConditionAndApproverList(String modelId) {
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());
        List<SetsCondition> conditionList = this.selectList(Condition.create().where("model_id={0}", modelId).and("enabled=1"));
        List<SetsProcess> setsProcessList = processService.selectList(Condition.create().where("model_id={0}", modelId).isNotNull("condition_id").orderBy("sort", true));
        List<ConditionAndApproverVO> conditionAndApproverVOS = new ArrayList<>();
        for (SetsCondition setsCondition : conditionList) {
            ConditionAndApproverVO conditionAndApproverVO = new ConditionAndApproverVO();
            conditionAndApproverVO.setConditionDesc("如果" + setsCondition.getContent());
            conditionAndApproverVO.setSort(setsCondition.getSort());
            conditionAndApproverVO.setConditionId(setsCondition.getId());
            List<UserVO> userVOList = new ArrayList<>();
            setsProcessList.parallelStream()
                    .filter(setsProcess -> setsProcess.getConditionId().equals(setsCondition.getId()))
                    .map(setsProcess -> {
                        UserVO vo = new UserVO();
                        String userNick = "";
                        String userAvatar = null;
                        String passportId = "";
                        String userId = setsProcess.getApprover();
                        if (userId.indexOf("admin_") != -1) {
                            String[] temp = String.valueOf(userId).split("_");
                            userNick = "第" + temp[2] + "级主管";
                        } else {
                            ApprovalUser user = userList.stream().filter(u -> u.getId().equals(userId)).distinct().findFirst().orElseGet(ApprovalUser::new);
                            userNick = user.getName();
                            passportId = user.getPassportId();
                            userAvatar = user.getAvatar();
                        }
                        vo.setMemberId(userId);
                        vo.setName(userNick);
                        vo.setPassportId(passportId);
                        vo.setProfile(userAvatar);
                        return userVOList.add(vo);
                    }).collect(Collectors.toList());

            conditionAndApproverVO.setApproverList(userVOList);
            conditionAndApproverVOS.add(conditionAndApproverVO);
        }
        return conditionAndApproverVOS.stream().sorted(Comparator.comparingInt(ConditionAndApproverVO::getSort).reversed()).distinct().collect(Collectors.toList());
    }

    @Override
    public boolean sortedCondition(String modelId, String sortArray) {
        boolean isUpdated = false;
        try {
            // 解析排序数据
            JSONArray sortJSONArray = JSONArray.parseArray(sortArray);
            // 封装排序集合为KEY-VALUE以部门编号为KEY，位置为VALUE
            Map<String, Integer> condtionSortMap = new HashMap<>(sortJSONArray.size());
            for (int i = 0; i < sortJSONArray.size(); i++) {
                JSONObject sortJSON = sortJSONArray.getJSONObject(i);
                condtionSortMap.put(sortJSON.getString("conditionId"), sortJSON.getInteger("sort"));
            }
            // 查询当前模板审批流程所有设置的条件
            List<SetsCondition> setsConditionList = this.selectList(Condition.create().where("model_id = {0}", modelId));
            if (setsConditionList != null && !setsConditionList.isEmpty()) {
                for (SetsCondition setsCondition : setsConditionList) {
                    Integer sort = condtionSortMap.get(setsCondition.getId());
                    setsCondition.setSort(sort != null ? sort : setsCondition.getSort());
                }
                isUpdated = this.updateBatchById(setsConditionList);
                if (isUpdated == false) {
                    throw new UpdateMessageFailureException("更新排序信息失败");
                }
            } else {
                throw new MessageNotExitException("当前审批模板下不存在设置的审批条件");
            }
        } catch (Exception e) {
            throw new MissingRequireFieldException("解析分组排序数据错误");
        }
        return isUpdated;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<SetConditionVO> saveSetsCondition(String modelId, String judge, String memberIds, String conditionId) {
        // 解析
        JSONArray jsonArray = JSON.parseArray(judge);
        Iterator<Object> it = jsonArray.iterator();
        List<ConditionVO> conditionVOList = new ArrayList<>();
        while (it.hasNext()) {
            JSONObject obj = (JSONObject) it.next();
            ConditionVO conditionVO = JSONObject.parseObject(obj.toJSONString(), ConditionVO.class);
            conditionVOList.add(conditionVO);
        }
        String condition = "";
        String content = "";
        String cdn1 = "";
        String cdn2 = "";
        String content1 = "";
        String content2 = "";
        int type = 0;
        for (ConditionVO conditionVO : conditionVOList) {
            if (StringUtils.isNotBlank(conditionVO.getValue())) {
                if (ApproConstants.RADIO_TYPE_3 == conditionVO.getType()) {
                    cdn1 = new StringBuilder(cdn1) + (conditionVO.getField() + " = " + conditionVO.getValue()) + "|";
                    content1 = new StringBuilder(content1) + (conditionVO.getLabel() + "属于：" + conditionVO.getValue().replaceAll(",", "或")) + " 并且 ";
                } else if (ApproConstants.NUMBER_TYPE_2 == conditionVO.getType()) {
                    cdn2 = conditionVO.getValue();
                    String[] split = cdn2.split(" ");
                    if (split.length > 3) {
                        Arrays.fill(split, 2, 3, conditionVO.getLabel());
                    } else {
                        Arrays.fill(split, 0, 1, conditionVO.getLabel());
                    }
                    content2 = StringUtils.join(Arrays.asList(split), " ");
                }

            }
            // 修改判断条件
            ModelItem modelItem = modelItemService.selectById(conditionVO.getModelItemId());
            if (modelItem != null) {
                modelItem.setIsJudge(conditionVO.getJudge());
                boolean isUpdated = modelItemService.updateById(modelItem);
                if (!isUpdated) {
                    throw new UpdateMessageFailureException("修改判断条件失败");
                }
            }
        }

        if (StringUtils.isBlank(cdn1) && StringUtils.isNotBlank(cdn2)) {
            condition = cdn2;
            content = content2;
            type = ApproConstants.NUMBER_TYPE_2;
        } else if (StringUtils.isNotBlank(cdn1) && StringUtils.isBlank(cdn2)) {
            condition = cdn1.substring(0, cdn1.length() - 1);
            content = content1.substring(0, content1.length() - 4);
            type = ApproConstants.RADIO_TYPE_3;
        } else if (StringUtils.isNotBlank(cdn1) && StringUtils.isNotBlank(cdn2)) {
            condition = cdn1.substring(0, cdn1.length() - 1) + "|" + cdn2;
            content = content1.substring(0, content1.length() - 4) + " 并且 " + content2;
            type = ApproConstants.RADIO_AND_NUMBER_TYPE_23;
        }
        List<SetsCondition> list = this.selectList(Condition.create().where("model_id={0}", modelId));
        Integer maxSort = list.stream().map(SetsCondition::getSort).max(Integer::compareTo).orElse(0);
        SetsCondition setsCondition = null;
        if (StringUtils.isNotBlank(conditionId)) {
            // 先删除旧的审批人
            processService.delete(modelId, conditionId);
            setsCondition = this.selectById(conditionId);
            setsCondition.setCdn(condition);
            setsCondition.setContent(content);
        } else {
            setsCondition = new SetsCondition();
            setsCondition.setId(IDUtils.uuid());
            setsCondition.setModelId(modelId);
            setsCondition.setEnabled(1);
            setsCondition.setCdn(condition);
            setsCondition.setType(type);
            setsCondition.setContent(content);
            setsCondition.setSort(maxSort + 1);
        }
        boolean insert = this.insertOrUpdate(setsCondition);
        if (!insert) {
            throw new InsertMessageFailureException("保存审批条件失败");
        }
        boolean b = processService.updateProcess(modelId, setsCondition.getId(), memberIds);
        if (!b) {
            throw new UpdateMessageFailureException("按条件保存审批人失败");
        }
        return this.getConditionList(modelId);
    }

}