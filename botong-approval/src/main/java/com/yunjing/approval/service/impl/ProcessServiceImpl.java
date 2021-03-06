package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ProcessMapper;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.Copy;
import com.yunjing.approval.model.entity.SetsProcess;
import com.yunjing.approval.model.vo.ApproverVO;
import com.yunjing.approval.model.vo.ConditionVO;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApprovalUtils;
import com.yunjing.message.share.org.OrgMemberMessage;
import com.yunjing.mommon.global.exception.DeleteMessageFailureException;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
@Service
public class ProcessServiceImpl extends BaseServiceImpl<ProcessMapper, SetsProcess> implements IProcessService {

    @Autowired
    private IConditionService conditionService;
    @Autowired
    private IProcessService processService;
    @Autowired
    private IModelItemService modelItemService;
    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private ICopyService copyService;
    @Autowired
    private AppCenterService appCenterService;

    @Override
    public boolean delete(String modelId, String conditionId) {
        Wrapper<SetsProcess> wrapper;
        if (StringUtils.isBlank(conditionId)) {
            wrapper = Condition.create().where("model_id={0}", modelId).isNull("condition_id");
        } else {
            wrapper = Condition.create().where("model_id={0}", modelId).and("condition_id={0}", conditionId);
        }
        boolean delete = this.delete(wrapper);
        if (!delete) {
            throw new DeleteMessageFailureException("清除审批流程人失败");
        }
        return delete;
    }

    @Override
    public List<UserVO> getProcess(String modelId, String conditionId) {
        List<UserVO> users = new ArrayList<>();
        List<SetsProcess> list;
        if (StringUtils.isNotBlank(conditionId)) {
            list = this.selectList(Condition.create().where("model_id={0}", modelId).in("condition_id", conditionId).orderBy(true, "sort", true));
        } else {
            list = this.selectList(Condition.create().where("model_id={0}", modelId).and("(condition_id is null or condition_id='')").orderBy(true, "sort", true));
        }
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());
        if (list != null && CollectionUtils.isNotEmpty(list)) {
            for (SetsProcess process : list) {
                String userId = process.getApprover();
                String passportId = "";
                String userNick = "";
                String userAvatar = null;
                if (userId.indexOf("admin_") != -1) {
                    String[] temp = String.valueOf(userId).split("_");
                    userNick = "第" + temp[2] + "级主管";

                } else {
                    ApprovalUser user = userList.stream().filter(approvalUser -> approvalUser.getId().equals(userId)).findFirst().orElseGet(ApprovalUser::new);
                    userNick = user.getName();
                    userAvatar = user.getAvatar();
                    passportId = user.getPassportId();
                }
                UserVO vo = new UserVO();
                vo.setMemberId(userId);
                vo.setName(userNick);
                vo.setProfile(userAvatar);
                vo.setPassportId(passportId);
                if (StringUtils.isNotBlank(vo.getName())) {
                    users.add(vo);
                }
            }
            return users;
        } else {
            return null;
        }
    }

    @Override
    public boolean updateProcess(String modelId, String conditionId, String userArray) {
        if (StringUtils.isNotBlank(userArray)) {
            String[] userIds = userArray.split(",");
            // 批量保存审批人信息
            List<SetsProcess> list = new ArrayList<>();
            for (int i = 0; i < userIds.length; i++) {
                SetsProcess process = new SetsProcess();
                process.setId(IDUtils.uuid());
                process.setModelId(modelId);
                process.setConditionId(conditionId);
                process.setApprover(userIds[i]);
                process.setSort(i + 1);
                list.add(process);
            }
            boolean insertBatch = this.insertBatch(list);
            if (!insertBatch) {
                throw new InsertMessageFailureException("批量保存审批人信息失败");
            }
            return true;
        } else {
            logger.info("审批Id为空");
            return false;
        }
    }

    @Override
    public ApproverVO getApprover(String companyId, String memberId, String modelId, String deptId, String judge) {

        // 管理端设置的主管审批人是否存在
        String isExistApprover = "";
        // 解析
        JSONArray jsonArray = JSON.parseArray(judge);
        Iterator<Object> it = jsonArray.iterator();
        List<ConditionVO> conditionVOList = new ArrayList<>();
        while (it.hasNext()) {
            JSONObject obj = (JSONObject) it.next();
            ConditionVO conditionVO = JSONObject.parseObject(obj.toJSONString(), ConditionVO.class);
            conditionVOList.add(conditionVO);
        }
        ApproverVO result = new ApproverVO();

        // 匹配管理端设置的审批条件
        List<ConditionVO> collect = conditionVOList.stream().distinct().collect(Collectors.toList());
        String conditionId = conditionService.getCondition(modelId, collect);
        result.setConditionId(conditionId);

        List<UserVO> users = processService.getProcess(modelId, conditionId);
        // 从应用中心获取部门主管
        Map<String, List<OrgMemberMessage>> deptManager = appCenterService.findDeptManager(companyId, memberId);
        List<UserVO> list = new ArrayList<>();

        if (users != null && users.size() > 0) {
            int index = 0;
            for (UserVO user : users) {
                if (user.getMemberId().indexOf("admin_") != -1) {
                    String[] temp = user.getMemberId().split("_");
                    int num = Integer.parseInt(temp[2]);
                    // 根据部门主键和级数查询出该主管
                    List<UserVO> admins = this.getAdmins(memberId, deptId, num, deptManager);
                    if (admins != null && CollectionUtils.isNotEmpty(admins)) {
                        list.addAll(admins);
                        index++;
                    } else {
                        index = -1;
                    }
                } else {
                    if (StringUtils.isNotBlank(user.getName())) {
                        list.add(user);
                        index++;
                    }else {
                        index = -1;
                    }
                }
            }
            if (index > 0) {
                /* 当且仅当所有主管都解析出了审批人或者选定的成员才为"true" ，否则为"false" */
                isExistApprover = "true";
            } else if (index < 0) {
                /* 只要有部门没有解析出主管对应的审批人为"false" */
                isExistApprover = "false";
            }
        }
        // 同一个审批人在流程中出现多次时，仅保留最后一个
        List<UserVO> distinctUserList = ApprovalUtils.removeDuplicate(list);
        if (CollectionUtils.isNotEmpty(distinctUserList)) {
            // 注入审批人
            result.setApprovers(distinctUserList);
            // 注入抄送人
            result.setCopys(copyService.get(modelId));
            result.setApproverShow(isExistApprover);
        } else {
            // 如果没有按条件设置的审批人，则显示默认审批人
            result = modelItemService.getDefaultApproverAndCopy(companyId, memberId, modelId, deptId);
            result.setLastApprovers(null);
            result.setLastCopys(null);
        }
        return result;
    }

    /**
     * 获取主管
     */
    @Override
    public List<UserVO> getAdmins(String memberId, String deptId, int num, Map<String, List<OrgMemberMessage>> deptManager) {
        List<UserVO> userVOList = new ArrayList<>();
        if (deptManager != null && MapUtils.isNotEmpty(deptManager)) {
            for (Map.Entry<String, List<OrgMemberMessage>> adminMember : deptManager.entrySet()) {
                if (adminMember.getKey().equals(deptId)) {
                    for (OrgMemberMessage admin : adminMember.getValue()) {
                        // 如果部门主管自己提交审批就略过
                        if (admin != null && memberId.equals(admin.getMemberId())) {
                            continue;
                        }
                        int n = num - 1;
                        num--;
                        if (admin != null && n == 0) {
                            UserVO vo = new UserVO();
                            vo.setMemberId(admin.getMemberId());
                            vo.setName(admin.getMemberName());
                            vo.setProfile(admin.getProfile());
                            vo.setPassportId(admin.getPassportId());
                            if (StringUtils.isNotBlank(vo.getName())) {
                                userVOList.add(vo);
                            }
                        }
                    }
                }
            }
        }
        return userVOList;
    }

    @Override
    public boolean saveDefaultApprover(String modelId, String approverIds, String copyIds) {
        boolean insertedCopy = false;
        boolean insertApprover = false;
        //先清除之前保存的默认审批人
        this.delete(modelId, null);
        // 清除默认抄送人
        copyService.delete(Condition.create().where("model_id={0}", modelId));
        if (StringUtils.isNotBlank(approverIds)) {
            String[] aIds = approverIds.split(",");
            // 批量保存审批人信息
            List<SetsProcess> list = new ArrayList<>();
            for (int i = 0; i < aIds.length; i++) {
                SetsProcess process = new SetsProcess();
                process.setId(IDUtils.uuid());
                process.setModelId(modelId);
                process.setApprover(aIds[i]);
                process.setSort(i + 1);
                list.add(process);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                insertApprover = this.insertBatch(list);
                if (!insertApprover) {
                    throw new InsertMessageFailureException("批量保存审批人信息失败");
                }
            }
        }
        if (StringUtils.isNotBlank(copyIds)) {
            String[] cIds = copyIds.split(",");
            List<Copy> copyList = new ArrayList<>();
            for (int i = 0; i < cIds.length; i++) {
                Copy copy = new Copy();
                copy.setId(IDUtils.uuid());
                copy.setType(0);
                copy.setModelId(modelId);
                copy.setSort(i + 1);
                copy.setUserId(cIds[i]);
                copyList.add(copy);
            }
            if (!copyList.isEmpty()) {
                insertedCopy = copyService.insertBatch(copyList);
                if (!insertedCopy) {
                    throw new InsertMessageFailureException("批量保存抄送人信息失败");
                }
            }
        }
        if (insertedCopy || insertedCopy) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ApproverVO getDefaultApprover(String modelId) {
        ApproverVO approverVO = new ApproverVO();
        List<UserVO> process = processService.getProcess(modelId, null);
        approverVO.setApprovers(process);
        List<UserVO> voList = copyService.get(modelId);
        approverVO.setCopys(voList);
        return approverVO;
    }
}