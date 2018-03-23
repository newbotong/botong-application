package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.approval.common.UUIDUtil;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.ProcessMapper;
import com.yunjing.approval.model.entity.ApprovalSets;
import com.yunjing.approval.model.entity.SetsProcess;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.IApprovalSetsService;
import com.yunjing.approval.service.IOrgModelService;
import com.yunjing.approval.service.IProcessService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.BaseException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author roc
 * @date 2017/12/21
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, SetsProcess> implements IProcessService {

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private IApprovalSetsService approvalSetsService;

    @Autowired
    private IOrgModelService orgModelService;

    @Autowired
    private ProcessMapper processMapper;

    @Override
    public boolean delete(String modelId, String conditions) throws Exception {

        if (StringUtils.isBlank(modelId)) {
            throw new BaseException("模型主键不存在");
        }

        Wrapper<SetsProcess> wrapper;

        if (StringUtils.isBlank(conditions)) {
            wrapper = Condition.create().where("model={0}", modelId).and("conditions=''").or("conditions is null");
        } else {
            wrapper = Condition.create().where("model={0}", modelId).and("conditions={0}", conditions);
        }

        this.delete(wrapper);

        return true;
    }

    @Override
    public List<UserVO> getProcess(String modelId, String conditions) throws Exception {
        List<UserVO> users = new ArrayList<>();
        List<SetsProcess> list;
        if (StringUtils.isBlank(conditions)) {
            list = this.selectList(Condition.create().where("model={0}", modelId).and("(conditions is null or conditions='')").orderBy(true, "sort", true));
        } else {
            list = this.selectList(Condition.create().where("model={0}", modelId).and("conditions={0}", conditions).orderBy(true, "sort", true));
        }

        for (SetsProcess process : list) {
            String userId = process.getApprover();
            String userNick = "";
            String userAvatar = null;
            if (userId.indexOf("admin_") != -1) {
                String[] temp = userId.split("_");
                userNick = "第" + temp[2] + "级主管";
            } else {
                UserVO userVo = userRedisService.getByUserId(userId);
                if (userVo != null) {
                    userNick = userVo.getUserNick();
                    userAvatar = userVo.getUserAvatar();
                }

            }
            UserVO vo = new UserVO();
            vo.setUserId(userId);
            vo.setUserNick(userNick);
            vo.setUserAvatar(userAvatar);
            users.add(vo);
        }
        return users;
    }

    @Override
    public boolean updateProcess(String modelId, String conditionId, String userArray) throws Exception {
        String[] userIds = userArray.split(",");
        int setting = ApproConstants.SET_TYPE_0;
        if (StringUtils.isNotBlank(conditionId)) {
            setting = ApproConstants.SET_TYPE_1;
        }
        if (StringUtils.isBlank(conditionId)) {
            conditionId = "";
        }
        ApprovalSets approvalSets = approvalSetsService.selectOne(Condition.create().where("model_id={0}", modelId));
        if (approvalSets == null) {
            approvalSets = new ApprovalSets();
            approvalSets.setModelId(modelId);
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
            process.setProcess(UUIDUtil.get());
            process.setModel(modelId);
            process.setConditions(conditionId);
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
    public void deleteProcessUser(String oid, String uid) {
        processMapper.deleteProcessUser(oid, uid);
    }
}