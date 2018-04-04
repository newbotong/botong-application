package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.CopyMapper;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.Copy;
import com.yunjing.approval.model.entity.OrgModel;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.ICopyService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author roc
 * @date 2017/12/21
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CopyServiceImpl extends BaseServiceImpl<CopyMapper, Copy> implements ICopyService {

    @Autowired
    private ICopyService copyService;

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private IOrgModelService orgModelService;

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
    public List<UserVO> get(Long modelId) throws Exception {
        OrgModel entity = orgModelService.selectOne(Condition.create().where("model_id={0}", modelId));
        Long oid = entity.getOrgId();
        if (null == oid) {
            throw new BaseException("模型所属企业不存在");
        }
        List<Copy> copys = copyService.selectList(Condition.create().where("model_id={0}", modelId).orderBy("sort", true));
        List<UserVO> userVos = new ArrayList<>();
        if (copys == null || copys.isEmpty()) {
            return userVos;
        }
        List<String> userIdList = new ArrayList<>(copys.size());
        for (Copy c : copys) {
            if (StringUtils.isBlank(c.getUserId())) {
                continue;
            }
            //0.用户 1.主管
            if (c.getType() == 0) {
                userIdList.add(c.getUserId());
            }
        }
        Map<String, UserVO> userVOMap = new HashMap<>(userIdList.size());
        Map<Long, ApprovalUser> userOrgVOMap = new HashMap<>(userIdList.size());
        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<UserVO> users = userRedisService.getByUserIdList(userIdList);

            if (CollectionUtils.isEmpty(users)) {
                throw new BaseException("无法获取用户缓存信息");
            }

            if (users.size() != userIdList.size()) {
                throw new BaseException("获取用户缓存信息数据异常");
            }

            for (UserVO vo : users) {
                userVOMap.put(String.valueOf(vo.getUserId()), vo);
            }

            // 调用企业服务
            List<ApprovalUser> userOrgs = approvalUserService.selectList(Condition.create());
            if (CollectionUtils.isEmpty(userOrgs)) {
                throw new BaseException("无法获取用户信息");
            }

            if (userOrgs.size() != userIdList.size()) {
                throw new BaseException("获取用户信息数据异常");
            }

            for (ApprovalUser uo : userOrgs) {
                userOrgVOMap.put(uo.getId(), uo);
            }
        }

        for (Copy c : copys) {
            UserVO vo = new UserVO();
            String userId = c.getUserId();

            if (StringUtils.isBlank(userId)) {
                continue;
            }

            if (c.getType() == 0) {
                vo = userVOMap.get(userId);
                ApprovalUser uo = userOrgVOMap.get(userId);
                if (uo != null) {
                    String name = uo.getName();
                    if (StringUtils.isNotBlank(name) && !vo.getUserNick().equals(name)) {
                        vo.setUserNick(name);
                    }
                }
            } else {
                vo.setUserId(userId);
            }

            userVos.add(vo);
        }

        return userVos;
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
    public boolean save(Long modelId, String userIds) throws Exception {

        if (null == modelId) {
            throw new BaseException("模型主键不存在");
        }

        if (StringUtils.isBlank(userIds)) {
            copyService.delete(Condition.create().eq("model_id", modelId));
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
            entity.setId(IDUtils.getID());
            entity.setModelId(modelId);
            entity.setUserId(id);
            entity.setSort(i);

            list.add(entity);
        }

        if (list.isEmpty()) {
            throw new BaseException("用户主键集合不存在");
        }

        copyService.delete(new EntityWrapper<Copy>().eq("model_id", modelId));

        return this.insertBatch(list);
    }

    @Override
    public boolean deleteCopyUser(Long oid, Long uid) {
        return copyService.deleteCopyUser(oid, uid);
    }
}