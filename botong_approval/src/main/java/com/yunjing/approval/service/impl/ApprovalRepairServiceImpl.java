package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.ApprovalMapper;
import com.yunjing.approval.model.entity.Approval;
import com.yunjing.approval.model.entity.ApprovalProcess;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.IApprovalProcessService;
import com.yunjing.approval.service.IApprovalRepairService;
import com.yunjing.approval.service.IModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author roc
 * @date 2018/1/15
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ApprovalRepairServiceImpl extends ServiceImpl<ApprovalMapper, Approval> implements IApprovalRepairService {

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private IModelService modelService;

    @Autowired
    private IApprovalProcessService approvalProcessService;

    @Override
    public List<Approval> repairTitle(String oid) throws Exception {
        Condition condition = Condition.create();

        if (StringUtils.isNotBlank(oid)) {
            condition.eq("org_id", oid);
        }

        List<Approval> list = this.selectList(condition);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Approval> entityList = new ArrayList<>();
            for (Approval approval : list) {
                if (StringUtils.isNotBlank(approval.getTitle())) {
                    continue;
                }

                String uid = approval.getUserId();
                String mid = approval.getModelId();

                UserVO userVO = userRedisService.getByUserId(uid);

                ModelL modelL = modelService.selectById(mid);

                String title = "不存在的";

                if (userVO == null) {
                    title += "用户";
                } else if (modelL == null) {
                    title += "模型";
                } else {
                    String nick = userVO.getUserNick();
                    String name = modelL.getModelName();

                    if (StringUtils.isBlank(nick)) {
                        title += "用户昵称";
                    } else if (StringUtils.isBlank(name)) {
                        title += "模型名称";
                    } else {
                        title = nick + "的" + name;
                    }
                }

                approval.setTitle(title);
                entityList.add(approval);
            }

            if (CollectionUtils.isNotEmpty(entityList)) {
                this.updateBatchById(entityList);
            }
            return entityList;
        }

        return list;
    }

    @Override
    public List<Approval> repairFinishTime(String oid) {
        Condition condition = Condition.create();

        if (StringUtils.isNotBlank(oid)) {
            condition.eq("org_id", oid);
        }

        List<Approval> list = this.selectList(condition.in("state", "1, 2"));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Approval> entityList = new ArrayList<>();

            // 小规模数据修复
            for (Approval approval : list) {
                if (approval.getFinishTime() != null) {
                    continue;
                }

                List<ApprovalProcess> processList = approvalProcessService.selectList(Condition.create().eq("approval_id", approval.getApprovalId()).orderBy("seq", true));
                if (CollectionUtils.isEmpty(processList)) {
                    continue;
                }
                ApprovalProcess process = processList.get(processList.size() - 1);
                if (process == null) {
                    continue;
                }

                approval.setFinishTime(process.getProcessTime());
                entityList.add(approval);
            }

            if (CollectionUtils.isNotEmpty(entityList)) {
                this.updateBatchById(entityList);
            }
            return entityList;
        }

        return null;
    }
}
