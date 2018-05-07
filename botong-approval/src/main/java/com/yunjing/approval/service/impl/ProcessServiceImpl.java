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
import com.yunjing.message.share.org.OrgMemberMessage;
import com.yunjing.mommon.global.exception.DeleteMessageFailureException;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private ProcessMapper processMapper;
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
            wrapper = Condition.create().where("model_id={0}", modelId).and("condition_id=''").or("condition_id is null");
        } else {
            wrapper = Condition.create().where("model_id={0}", modelId).and("condition_id={0}", conditionId);
        }
        boolean delete = this.delete(wrapper);
        if(!delete){
            throw new DeleteMessageFailureException("清除审批流程人失败");
        }
        return delete;
    }

    @Override
    public List<UserVO> getProcess(String modelId, List<String> conditionIds) {

        List<UserVO> users = new ArrayList<>();
        List<SetsProcess> list;
        if (conditionIds != null && !conditionIds.isEmpty()) {
            list = this.selectList(Condition.create().where("model_id={0}", modelId).in("condition_id", conditionIds).orderBy(true, "sort", true));
        } else {
            list = this.selectList(Condition.create().where("model_id={0}", modelId).and("(condition_id is null or condition_id='')").orderBy(true, "sort", true));
        }
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());

        for (SetsProcess process : list) {
            String userId = process.getApprover();
            String passportId = "";
            String userNick = "";
            String userAvatar = null;
            if (userId.indexOf("admin_") != -1) {
                String[] temp = String.valueOf(userId).split("_");
                userNick = "第" + temp[2] + "级主管";

            } else {
                Set<ApprovalUser> userSet = userList.stream().filter(user -> user.getId().equals(userId)).collect(Collectors.toSet());
                for (ApprovalUser user : userSet) {
                    if (user != null) {
                        userNick = user.getName();
                        userAvatar = user.getAvatar();
                        passportId = user.getPassportId();
                    }
                }

            }
            UserVO vo = new UserVO();
            vo.setMemberId(userId);
            vo.setName(userNick);
            vo.setProfile(userAvatar);
            vo.setPassportId(passportId);
            users.add(vo);
        }
        return users;
    }

    @Override
    public boolean updateProcess(String modelId, String conditionId, String userArray) {
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
    }

    @Override
    public void deleteProcessUser(String companyId, String memberId) {
        processMapper.deleteProcessUser(companyId, memberId);
    }

    @Override
    public ApproverVO getApprover(String companyId, String memberId, String modelId, String deptId, String judge) {
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
        List<String> conditionIds = new ArrayList<>();
        for (ConditionVO conditionVO : conditionVOList) {
            String id = conditionService.getCondition(modelId, conditionVO);
            conditionIds.add(id);
        }
        result.setConditionId(conditionIds);
        List<UserVO> users = processService.getProcess(modelId, conditionIds);

        // 从应用中心获取部门主管
        Map<String, List<OrgMemberMessage>> deptManager = appCenterService.findDeptManager(companyId, memberId);
        List<UserVO> list = new ArrayList<>();
        if (users != null && users.size() > 0) {
            for (UserVO user : users) {
                if (user.getMemberId().indexOf("admin_") != -1) {
                    String[] temp = user.getMemberId().split("_");
                    int num = Integer.parseInt(temp[2]);
                    // 根据部门主键和级数查询出该主管
                    List<UserVO> admins = getAdmins(deptId, num, deptManager);
                    if (admins != null && CollectionUtils.isNotEmpty(admins)) {
                        list.addAll(admins);
                    }
                } else {
                    list.add(user);
                }
            }
        }
        // 注入审批人
        List<UserVO> distinctUserList = list.stream().distinct().collect(Collectors.toList());
        result.setApprovers(distinctUserList);
        // 注入抄送人
        result.setCopys(copyService.getCopy(companyId, memberId, modelId));

        return result;
    }

    /**
     * 获取主管
     */
    public List<UserVO> getAdmins(String deptId, int num, Map<String, List<OrgMemberMessage>> deptManager) {
        List<UserVO> userVOList = new ArrayList<>();
        if (deptManager != null && MapUtils.isNotEmpty(deptManager)) {
            deptManager.forEach((s, orgMemberMessages) -> {
                if (s.equals(deptId)) {
                    int nums = num - 1;
                    if (nums == 0) {
                        for (OrgMemberMessage admin : orgMemberMessages) {
                            if (admin != null) {
                                UserVO vo = new UserVO();
                                vo.setName(admin.getMemberName());
                                vo.setMemberId(admin.getMemberId());
                                vo.setProfile(admin.getProfile());
                                vo.setPassportId(admin.getPassportId());
                                userVOList.add(vo);
                            }
                        }
                    } else {
                        if (deptId != null) {
                            List<UserVO> admin = getAdmins(deptId, num, deptManager);
                            if (admin != null && CollectionUtils.isNotEmpty(admin)) {
                                userVOList.addAll(admin);
                            }
                        }
                    }
                }
            });
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
        if (StringUtils.isNotBlank(approverIds)){
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
        if(StringUtils.isNotBlank(copyIds)){
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
        if(insertedCopy || insertedCopy){
            return true;
        }else {
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