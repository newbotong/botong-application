package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ConditionMapper;
import com.yunjing.approval.dao.mapper.ProcessMapper;
import com.yunjing.approval.model.entity.*;
import com.yunjing.approval.model.vo.ApproverVO;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.*;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.approval.util.Colors;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author roc
 * @date 2017/12/21
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProcessServiceImpl extends BaseServiceImpl<ProcessMapper, SetsProcess> implements IProcessService {


    @Autowired
    private IApprovalSetsService approvalSetsService;

    @Autowired
    private ConditionMapper conditionMapper;
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

    @Override
    public boolean delete(String modelId, String conditions) throws Exception {

        if (null == modelId) {
            throw new BaseException("模型主键不存在");
        }

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
    public List<UserVO> getProcess(String modelId, String conditionId) throws Exception {
        List<UserVO> users = new ArrayList<>();
        List<SetsProcess> list;
        if (StringUtils.isBlank(conditionId)) {
            list = this.selectList(Condition.create().where("model_id={0}", modelId).and("(condition_id is null or condition_id='')").orderBy(true, "sort", true));
        } else {
            list = this.selectList(Condition.create().where("model_id={0}", modelId).and("condition_id={0}", conditionId).orderBy(true, "sort", true));
        }
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());
        for (SetsProcess process : list) {
            String userId = process.getApprover();
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
                    }
                }

            }
            UserVO vo = new UserVO();
            vo.setMemberId(userId);
            vo.setName(userNick);
            vo.setProfile(userAvatar);
            users.add(vo);
        }
        return users;
    }

    @Override
    public boolean updateProcess(String modelId, String conditionId, String userArray) throws Exception {
        String[] userIds = userArray.split(",");
        int setting = ApproConstants.SET_TYPE_0;
        if (null != conditionId) {
            setting = ApproConstants.SET_TYPE_1;
        }
        if (null == conditionId) {
            conditionId = null;
        }
        ApprovalSets approvalSets = approvalSetsService.selectOne(Condition.create().where("model_id={0}", modelId));
        if (approvalSets == null) {
            approvalSets = new ApprovalSets();
            approvalSets.setId(modelId);
        }
        approvalSets.setSetting(setting);
        boolean insertOrUpdate = approvalSetsService.insertOrUpdate(approvalSets);
        if (!insertOrUpdate) {
            throw new BaseException("审批设置项保存失败");
        }
        // 先删除旧的审批条件，再设置新的审批条件
        this.delete(modelId, conditionId);

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
            throw new BaseException("批量保存审批人信息失败");
        }
        return true;
    }

    @Override
    public void deleteProcessUser(String companyId, String memberId) {
        processMapper.deleteProcessUser(companyId, memberId);
    }

    @Override
    public ApproverVO getApprover(String companyId, String memberId, String modelId, String deptId, String conditionId, String field,String value) throws Exception {
        ApproverVO result = new ApproverVO();
        // 获取审批流程设置类型，set=0:不分条件设置审批人 set=1:分条件设置审批人
        ApprovalSets sets = approvalSetsService.selectOne(Condition.create().where("model_id={0}", modelId));
        if (sets != null) {
            String cdnId = "";
            // 分条件审批
            if (sets.getSetting() == 1) {
                if (StringUtils.isBlank(deptId)) {
                    //rpc获取成员所在的根部门信息
                    result.setDeptName("互联网时代");
                }
                if (StringUtils.isNotBlank(value)) {
                    conditionId = conditionService.getCondition(modelId, value);
                }

                if (StringUtils.isBlank(conditionId)) {
                    List<SetsCondition> conditionSet = conditionService.getFirstCondition(modelId);
                    for (SetsCondition sc : conditionSet) {
                        String s = sc.getCdn().substring(0, sc.getCdn().indexOf(" "));
                        if (sc != null && s.equals(field)) {
                            cdnId = sc.getId();
                        }
                    }
                } else {
                    cdnId = conditionId;
                }

                if (StringUtils.isBlank(cdnId)) {
                    return null;
                }
                result.setConditionId(cdnId);
            }

            List<UserVO> users = processService.getProcess(modelId, cdnId);

            List<UserVO> list = new ArrayList<>();

            if (users != null && users.size() > 0) {
                // 根据部门主键和成员主键判断是不是主管
                boolean flag = isAdmins(deptId, memberId);

                for (UserVO user : users) {
                    if (user.getMemberId().indexOf("admin_") != -1) {
                        String[] temp = user.getMemberId().split("_");
                        int num = Integer.parseInt(temp[2]);
                        if (flag) {
                            num++;
                        }
                        // 根据部门主键和级数查询出该主管
                        List<UserVO> admins = getAdmins(deptId, num);
                        if (admins != null && admins.size() > 0) {
                            for (UserVO admin : admins) {
                                list.add(admin);
                            }
                        }
                    } else {
                        list.add(user);
                    }
                }
            }
            if (list != null && list.size() > 0) {
                result.setApproverList(list);
            }
            // 获取抄送人
            result.setCopyList(copyService.getCopy(companyId,memberId,modelId));

            return result;
        }
        return null;
    }

    /**
     * 判断是不是主管
     */
    public boolean isAdmins(String deptId, String userId) {
        return false;
    }

    /**
     * 获取主管
     */
    public List<UserVO> getAdmins(String deptId, int num) {
        return null;
    }

}