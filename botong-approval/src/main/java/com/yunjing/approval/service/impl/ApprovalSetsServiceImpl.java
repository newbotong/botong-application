package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalSetsMapper;
import com.yunjing.approval.model.entity.ApprovalSets;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.ApprovalSetVO;
import com.yunjing.approval.model.vo.SetConditionVO;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.IApprovalSetsService;
import com.yunjing.approval.service.IConditionService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.approval.service.IProcessService;
import com.yunjing.approval.util.ApproConstants;
import com.yunjing.mommon.global.exception.InsertMessageFailureException;
import com.yunjing.mommon.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2017/11/30
 */
@Service
public class ApprovalSetsServiceImpl extends ServiceImpl<ApprovalSetsMapper, ApprovalSets> implements IApprovalSetsService {


    @Autowired
    private IModelService modelService;
    @Autowired
    private IProcessService processService;
    @Autowired
    private IConditionService conditionService;

    @Override
    public ApprovalSetVO getApprovalSet(String modelId) throws Exception {
        ApprovalSetVO approvalSetVO = new ApprovalSetVO();
        ModelL modelL = modelService.selectById(modelId);
        approvalSetVO.setModelL(modelL);

        // 不分条件设置审批
        List<UserVO> userVoList = processService.getProcess(modelId, null);
        approvalSetVO.setUsers(userVoList);

        // 分条件
        List<SetConditionVO> conditionList = conditionService.getConditionList(modelId);
        approvalSetVO.setList(conditionList);

        // 设置
        ApprovalSets approvalSets = this.selectOne(Condition.create().where("model_id={0}", modelId));
        if (approvalSets != null && approvalSets.getSetting() == ApproConstants.SET_TYPE_1) {
            approvalSetVO.setSetting(ApproConstants.SET_TYPE_1);
        } else if (approvalSets != null && approvalSets.getSetting() == ApproConstants.SET_TYPE_0) {
            approvalSetVO.setSetting(ApproConstants.SET_TYPE_0);
        } else {
            approvalSetVO.setSetting(ApproConstants.SET_TYPE_2);
        }
        return approvalSetVO;
    }

    @Override
    public boolean saveApprovalSets(String modelId, Integer setting) throws Exception {
        ApprovalSets approvalSets = this.selectById(modelId);
        if (setting == ApproConstants.SET_TYPE_2) {
            if (approvalSets != null) {
                this.delete(Condition.create().where("model_id={0}", modelId));
            }
        } else {
            if (approvalSets == null) {
                approvalSets = new ApprovalSets();
                approvalSets.setModelId(modelId);
                approvalSets.setId(IDUtils.uuid());
            }
            approvalSets.setSetting(setting);
            boolean insertOrUpdate = this.insertOrUpdate(approvalSets);
            if (!insertOrUpdate) {
                throw new InsertMessageFailureException("审批设置项保存失败");
            }

        }
        return true;
    }

}
