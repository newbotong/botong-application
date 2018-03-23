package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.approval.common.DateUtil;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.ApprovalProcessMapper;
import com.yunjing.approval.dao.mapper.ModelLMapper;
import com.yunjing.approval.model.entity.ApprovalProcess;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.ApprovalContent;
import com.yunjing.approval.model.vo.ClientApprovalVO;
import com.yunjing.approval.model.vo.ClientModelVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IModelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liuxiaopeng
 * @date 2018/02/28
 */
@Service

public class ApprovalApiServiceImpl implements IApprovalApiService {

    @Autowired
    private IModelService modelService;
    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private ModelLMapper modelLMapper;

    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ClientModelVO> getList(Long orgId) {
        List<ModelVO> modelVOS = modelLMapper.selectModelListByOrgId(orgId);
        List<ClientModelVO> list = new ArrayList<>();
        for (ModelVO modelVO : modelVOS) {
            ClientModelVO modelVO1 = new ClientModelVO(modelVO);
            list.add(modelVO1);
        }
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Page<ClientApprovalVO> getWaited(Page page, Long orgId, Long userId, Integer state, String searchKey) {
        int current = page.getCurrent();
        int size = page.getSize();
        int index = (current - 1) * size;
        Page<ApprovalContent> approvalContentPage = new Page<>();

        //根据用户ID和企业ID查询审批数据
        //判断searchKey类型 true时间 false 查询关键字
        boolean flag = true;
        if (StringUtils.isNotBlank(searchKey)) {
            searchKey = searchKey.replace(".", "-").replace("/", "-");
            try {
                Date date = DateUtil.dateFormat(searchKey, DateUtil.DATE_FORMAT_2);
            } catch (ParseException e) {
                flag = false;
            }
        }
        List<ApprovalContent> approvalContents = approvalProcessMapper.getMyApprovalList(index, size,orgId, userId, searchKey,true);
        List<ModelL> modelLList = modelService.selectList(Condition.create().where("org_id", orgId));
        for (ApprovalContent approvalContent : approvalContents) {
//            if (selectApprovalContent(approce) == null) {
//                continue;
//            }
//            if (StringUtils.isNotBlank(searchKey)) {
//                if (!flag) {
//                    List<ModelL> models = modelLList.stream().filter(modelL -> modelL.getModelId().equals(approvalContent.getModelId())).collect(Collectors.toList());
//                    String modelName = "";
//                    for (ModelL model : models) {
//                        modelName = model.getModelName();
//                    }
//                    String str = userRedisService.getByUserId(approvalContent.getUserId()).getUserNick() + "的" + modelName;
//
//                    if (str.indexOf(searchKey) != -1) {
//                        approvalContents.add(selectApprovalContent(approce));
//                    }
//                } else {
//                    approvalContents.add(selectApprovalContent(approce));
//                }
//            } else {
//                approvalContents.add(selectApprovalContent(approce));
//            }
        }
        return null;
    }

    /**
     * 根据查询结果及条件获取审批数据
     */
    public ApprovalContent selectApprovalContent(ApprovalProcess approce) {

//        //第一个审批人
//        if (approce.getSeq() == 1) {
//            return getApprovalContent(approce);
//        } else {
//            List<ApprovalProcess> approvas = processdao.getList("approval", approce.getApproval());
//            for (ApprovalProcess appces : approvas) {
//                //判断上一个审批人的审批状态，如果是1（同意）或者是3（转让）显示当前审批人
//                if (appces.getSeq() + 1 == approce.getSeq() && appces.getState() == 1) {
//                    return getApprovalContent(approce);
//                }
//            }
//        }
        return null;
    }

    /**
     * 获取待审批数据对象
     */

    public ApprovalContent getApprovalContent(ApprovalProcess approce) {
//        Colors color = new Colors();
        ApprovalContent approval = new ApprovalContent();
//        approval.setApprovalId(approce.getApproval().getApprovalId());
//        approval.setCreateTime(approce.getApproval().getCreateTime());
//        approval.setModelName(approce.getApproval().getModel().getModelName());
//        approval.setState(approce.getApproval().getState());
//        if (approce.getApproval().getUser().getUserAvatar() == null) {
//            approval.setUserAvatar("");
//        } else {
//            approval.setUserAvatar(approce.getApproval().getUser().getUserAvatar());
//        }
//        //头像背景
//        approval.setColor(color.generateBeautifulColor(approce.getApproval().getUser().getUserMobile(), approce.getApproval().getUser().getUserNick()));
//        approval.setUserId(approce.getApproval().getUser().getUserId());
//        approval.setUserNick(approce.getApproval().getUser().getUserNick());
//        if (approce.getApproval().getResult() == null) {
//            approval.setMsg("审批中");
//        } else if (approce.getApproval().getResult() == 1) {
//            approval.setMsg("审批完成（同意）");
//        } else if (approce.getApproval().getResult() == 2) {
//            approval.setMsg("审批完成（拒绝）");
//        } else if (approce.getApproval().getResult() == null && approce.getApproval().getState() == 0) {
//            approval.setMsg("待审批");
//        } else {
//            approval.setMsg("");
//        }
        return approval;
    }

}
