package com.yunjing.approval.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ProcessMapper;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.model.vo.ApproverVO;
import com.yunjing.approval.model.vo.ConditionVO;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.message.share.org.OrgMemberMessage;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
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
    private IApprovalSetsService approvalSetsService;

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
    public boolean delete(String modelId, String conditions) {
        Wrapper<SetsProcess> wrapper;
        if (null == conditions) {
            wrapper = Condition.create().where("model_id={0}", modelId).and("condition_id=''").or("condition_id is null");
        } else {
            wrapper = Condition.create().where("model_id={0}", modelId).and("condition_id={0}", conditions);
        }
        this.delete(wrapper);

        return true;
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
        // 获取审批流程设置类型，set=0:不分条件设置审批人 set=1:分条件设置审批人
        ApprovalSets sets = approvalSetsService.selectOne(Condition.create().where("model_id={0}", modelId));
        if (sets != null) {
            List<String> cdnIds = new ArrayList<>();
            // 分条件审批
            if (sets.getSetting() == 1) {
                List<String> conditionIds = new ArrayList<>();
                for (ConditionVO conditionVO : conditionVOList) {
                    if (ApproConstants.RADIO_TYPE_3 == conditionVO.getType()) {
                        String id = conditionService.getCondition(modelId, conditionVO);
                        conditionIds.add(id);
                    }
                }
                if (conditionIds.isEmpty()) {
                    List<SetsCondition> conditionSet = conditionService.getFirstCondition(modelId);
                    for (SetsCondition sc : conditionSet) {
                        String field = sc.getCdn().substring(0, sc.getCdn().indexOf(" "));
                        String value = sc.getCdn().substring(sc.getCdn().lastIndexOf(" "), sc.getCdn().length()).trim();
                        for (ConditionVO conditionVO : conditionVOList) {
                            if (sc != null && field.equals(conditionVO.getField()) && conditionVO.getValue().equals(value)) {
                                cdnIds.add(sc.getId());
                            }
                        }
                    }
                } else {
                    cdnIds.addAll(conditionIds);
                }
                if (cdnIds.isEmpty()) {
                    return null;
                }
                result.setConditionId(cdnIds);
            }
            List<UserVO> users = processService.getProcess(modelId, cdnIds);
            List<UserVO> list = new ArrayList<>();
            if (users != null && users.size() > 0) {
                for (UserVO user : users) {
                    if (user.getMemberId().indexOf("admin_") != -1) {
                        String[] temp = user.getMemberId().split("_");
                        int num = Integer.parseInt(temp[2]);
                        // 根据部门主键和级数查询出该主管
                        List<UserVO> admins = getAdmins(companyId, memberId, deptId, num);
                        if (admins != null && CollectionUtils.isNotEmpty(admins)) {
                            list.addAll(admins);
                        }
                    } else {
                        list.add(user);
                    }
                }
            }
            // 注入审批人
            result.setApprovers(list);
            // 注入抄送人
            result.setCopys(copyService.getCopy(companyId, memberId, modelId));

            return result;
        }
        return null;
    }

    /**
     * 获取主管
     */
    public List<UserVO> getAdmins(String companyId, String memberId, String deptId, int num) {
        Map<String, List<OrgMemberMessage>> deptManager = appCenterService.findDeptManager(companyId, memberId);
        List<UserVO> userVOList = new ArrayList<>();
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
                        List<UserVO> admin = getAdmins(companyId, memberId, deptId, num);
                        if (admin != null && CollectionUtils.isNotEmpty(admin)) {
                            userVOList.addAll(admin);
                        }
                    }
                }
            }
        });
        return userVOList;
    }

    @Override
    public boolean saveDefaultApprover(String modelId, String approverIds, String copyIds) {
        boolean isInserted = false;
        //先清除之前保存的默认审批人
        this.delete(modelId, null);
        // 清除默认抄送人
        copyService.delete(Condition.create().where("model_id={0}", modelId));
        String[] aIds = approverIds.split(",");
        String[] cIds = copyIds.split(",");
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
        if (!list.isEmpty()) {
            boolean insertBatch = this.insertBatch(list);
            if (!insertBatch) {
                throw new InsertMessageFailureException("批量保存审批人信息失败");
            }
        }
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
            isInserted = copyService.insertBatch(copyList);
            if (!isInserted) {
                throw new InsertMessageFailureException("批量保存抄送人信息失败");
            }
        }
        return isInserted;
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