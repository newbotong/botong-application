package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.CopyMapper;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.Copy;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.ICopyService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author roc
 * @date 2017/12/21
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CopyServiceImpl extends BaseServiceImpl<CopyMapper, Copy> implements ICopyService {

    @Autowired
    private CopyMapper copyMapper;

    @Autowired
    private IApprovalUserService approvalUserService;

    /**
     * 获取抄送人
     *
     * @param modelId 模型编号
     * @return
     * @throws Exception
     */
    @Override
    public List<UserVO> get(String modelId) throws Exception {
        List<Copy> copyList = this.selectList(Condition.create().where("model_id={0}", modelId).orderBy("sort", true));
        List<UserVO> userVOList = new ArrayList<>();
        List<Long> ids = new ArrayList<>(copyList.size());
        for (Copy c : copyList) {
            //0.用户 1.主管
            if (c.getType() == 0) {
                ids.add(Long.valueOf(c.getUserId()));
            }
        }
        List<ApprovalUser> users = approvalUserService.selectList(Condition.create().in("id", ids));
        if (users != null && !users.isEmpty()) {
            for (Copy copy : copyList) {
                UserVO userVO = new UserVO();
                List<ApprovalUser> userList = users.stream().filter(user -> String.valueOf(user.getId()).equals(copy.getUserId())).collect(Collectors.toList());
                if (userList != null && !userList.isEmpty() && copy.getType() == 0) {
                    ApprovalUser user = userList.get(0);
                    userVO.setMemberId(user.getId());
                    userVO.setProfile(user.getAvatar());
                    userVO.setName(user.getName());
                } else {
                    userVO.setMemberId(copy.getUserId());
                }
                userVOList.add(userVO);
            }
        }
        return userVOList;
    }

    @Override
    public List<UserVO> getCopy(String companyId, String memberId, String modelId) throws Exception {
        List<Copy> copyList = this.selectList(Condition.create().where("model_id={0}", modelId).orderBy("sort", true));
        String[] deptIds = approvalUserService.selectById(memberId).getDeptId().split(",");
        List<ApprovalUser> userList = new ArrayList<>();
        if (copyList != null && !copyList.isEmpty()) {
            for (Copy copy : copyList) {
                if (copy.getType() == 0) {
                    ApprovalUser user = approvalUserService.selectById(copy.getUserId());
                    if (user != null && selectList(userList, user.getId())) {
                        userList.add(user);
                    }
                } else {
                    // 查询成员所在部门
                    for (String deptId : deptIds) {
                        String[] erids = copy.getUserId().split("_");
                        getAdmin(deptId, Integer.parseInt(erids[2]));
                    }
                }
            }
        }
        //处理用户头像
        List<UserVO> uservos = new ArrayList<>();
        for (ApprovalUser user : userList) {
            UserVO uservo = new UserVO();
            uservo.setMemberId(user.getId());
            uservo.setName(user.getName());
            if (user.getAvatar() != null && !"".equals(user.getAvatar())) {
                uservo.setProfile(user.getAvatar());
            } else {
                uservo.setColor(user.getColor()!= null ? user.getColor(): ApproConstants.DEFAULT_COLOR);
            }
            uservos.add(uservo);
        }
        return uservos;
    }

    /**
     * 判断list是否存在ID
     */
    public boolean selectList(List<ApprovalUser> list, String id) {
        for (ApprovalUser user : list) {
            if (user.getId().equals(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 递归查找部门主管
     **/
    public void getAdmin(String deptId, int num) {
//        Dept dept = deptDao.get(deptId);
//        int nums = num - 1;
//        if (nums == 0) {
//
//            List<DeptAdmin> deptAdmin = deptAdminDao.getList("deptId", dept.getDeptId());
//            for (DeptAdmin admin : deptAdmin) {
//                if (admin.getAdminId() != null) {
//                    User _user = userDao.get(admin.getAdminId());
//                    if (selectList(userList, _user.getUserId())) {
//                        userList.add(_user);
//                    }
//                }
//            }
//        } else {
//            if (dept.getDept() != null) {
//                getAdmin(dept.getDept().getDeptId(), nums);
//            }
//        }
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
    public boolean save(String modelId, String userIds) throws Exception {

        if (null == modelId) {
            throw new BaseException("模型主键不存在");
        }

        if (StringUtils.isBlank(userIds)) {
            copyMapper.delete(Condition.create().eq("model_id", modelId));
            return true;
        }

        String[] ids = StringUtils.split(userIds, ",");

        if (ArrayUtils.isEmpty(ids)) {
            throw new BaseException("用户主键集合不存在");
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
            throw new BaseException("用户主键集合不存在");
        }

        this.delete(new EntityWrapper<Copy>().eq("model_id", modelId));

        return this.insertBatch(list);
    }

    @Override
    public boolean deleteCopyUser(String companyId, String memberId) {
        return copyMapper.deleteCopyUser(companyId, memberId);
    }
}