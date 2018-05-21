package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.CopyMapper;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.Copy;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.ICopyService;
import com.yunjing.approval.service.IProcessService;
import com.yunjing.approval.util.ApprovalUtils;
import com.yunjing.message.share.org.OrgMemberMessage;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2017/12/21
 */
@Service
public class CopyServiceImpl extends BaseServiceImpl<CopyMapper, Copy> implements ICopyService {

    @Autowired
    private CopyMapper copyMapper;
    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private AppCenterService appCenterService;
    @Autowired
    private IProcessService processService;

    /**
     * 获取抄送人
     *
     * @param modelId 模型编号
     * @return
     * @throws Exception
     */
    @Override
    public List<UserVO> get(String modelId) {
        List<Copy> copyList = this.selectList(Condition.create().where("model_id={0}", modelId).orderBy("sort", true));
        List<UserVO> userVOList = new ArrayList<>();
        List<String> ids = new ArrayList<>(copyList.size());
        for (Copy c : copyList) {
            //0.用户 1.主管
            if (c.getType() == 0) {
                ids.add(c.getUserId());
            }
        }
        List<ApprovalUser> users = approvalUserService.selectList(Condition.create().in("id", ids));
        if (users != null && !users.isEmpty()) {
            for (Copy copy : copyList) {
                UserVO userVO = new UserVO();
                ApprovalUser user = users.stream().filter(approvalUser -> String.valueOf(approvalUser.getId()).equals(copy.getUserId())).findFirst().orElse(null);
                if (user != null && copy.getType() == 0) {
                    userVO.setMemberId(user.getId());
                    userVO.setProfile(user.getAvatar());
                    userVO.setName(user.getName());
                    userVO.setPassportId(user.getPassportId());
                }
                userVOList.add(userVO);
            }
        }
        return userVOList;
    }

    @Override
    public List<UserVO> getCopy(String companyId, String memberId, String modelId, String deptId) {
        List<Copy> copyList = this.selectList(Condition.create().where("model_id={0}", modelId).orderBy("sort", true));
        String[] deptIds = approvalUserService.selectById(memberId).getDeptId().split(",");
        Map<String, List<OrgMemberMessage>> deptManager = appCenterService.findDeptManager(companyId, memberId);
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.EMPTY);
        List<UserVO> userVOList = new ArrayList<>();
        if (copyList != null && !copyList.isEmpty()) {
            for (Copy copy : copyList) {
                // 抄送人为个人时 0：个人，1：主管
                if (copy.getType() == 0) {
                    ApprovalUser user = userList.stream()
                            .filter(approvalUser -> approvalUser.getId().equals(copy.getUserId()))
                            .findFirst().orElseGet(ApprovalUser::new);
                    UserVO userVO = new UserVO();
                    userVO.setMemberId(user.getId());
                    userVO.setProfile(user.getAvatar());
                    userVO.setName(user.getName());
                    userVO.setPassportId(user.getPassportId());
                    if (StringUtils.isNotBlank(userVO.getName())) {
                        userVOList.add(userVO);
                    }
                } else {
                    String[] temp = copy.getUserId().split("_");
                    int num = Integer.parseInt(temp[2]);
                    if (StringUtils.isBlank(deptId)) {
                        deptId = deptIds[0];
                    }
                    List<UserVO> admin = processService.getAdmins(companyId, deptId, num, deptManager);
                    if (admin != null && CollectionUtils.isNotEmpty(admin)) {
                        userVOList.addAll(admin);
                    }
                }
            }
        }
        // 同一个审批人在流程中出现多次时，仅保留最后一个
        return ApprovalUtils.removeDuplicate(userVOList);
    }

    /**
     * 保存抄送人
     *
     * @param modelId 模型编号
     * @param userIds 用户集合
     * @return
     * @throws Exception
     */
    @Override
    public boolean save(String modelId, String userIds) {

        if (null == modelId) {
            throw new ParameterErrorException("模型主键不存在");
        }

        if (StringUtils.isBlank(userIds)) {
            copyMapper.delete(Condition.create().eq("model_id", modelId));
            return true;
        }

        String[] ids = StringUtils.split(userIds, ",");

        if (ArrayUtils.isEmpty(ids)) {
            throw new ParameterErrorException("用户主键集合不存在");
        }

        List<Copy> list = new ArrayList<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];

            if (StringUtils.isBlank(id)) {
                continue;
            }

            Copy entity = new Copy();
            if (id.contains("admin_")) {
                entity.setType(1);
            } else {
                entity.setType(0);
            }
            entity.setId(IDUtils.uuid());
            entity.setModelId(modelId);
            entity.setUserId(id);
            entity.setSort(i);

            list.add(entity);
        }

        if (list.isEmpty()) {
            throw new ParameterErrorException("用户主键集合不存在");
        }

        this.delete(new EntityWrapper<Copy>().eq("model_id", modelId));

        return this.insertBatch(list);
    }

    @Override
    public boolean deleteCopyUser(String companyId, String memberId) {
        return copyMapper.deleteCopyUser(companyId, memberId);
    }
}