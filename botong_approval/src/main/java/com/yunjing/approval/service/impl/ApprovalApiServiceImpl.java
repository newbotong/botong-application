package com.yunjing.approval.service.impl;

import com.common.mybatis.page.Page;
import com.yunjing.approval.common.Colors;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.ApprovalProcessMapper;
import com.yunjing.approval.dao.mapper.CopySMapper;
import com.yunjing.approval.dao.mapper.ModelLMapper;
import com.yunjing.approval.model.dto.ApprovalContentVO;
import com.yunjing.approval.model.vo.ClientApprovalVO;
import com.yunjing.approval.model.vo.ClientModelVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IModelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private CopySMapper copysMapper;

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
    public Page<ClientApprovalVO> getWaited(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentVO> waitedMeApprovalList = approvalProcessMapper.getWaitedMeApprovalList(index, size, orgId, userId, searchKey, flag);
        convertList(clientApprovalVOS, waitedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Page<ClientApprovalVO> getCompleted(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentVO> completedMeApprovalList = approvalProcessMapper.getCompletedApprovalList(index, size, orgId, userId, searchKey, flag);
        convertList(clientApprovalVOS, completedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getLaunched(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentVO> launchedMeApprovalList = approvalProcessMapper.getLaunchedApprovalList(index, size, orgId, userId, searchKey, flag);
        String message = "";
        int i = 1;
        for (ApprovalContentVO contentVO : launchedMeApprovalList) {
            if (contentVO.getProcessState() == 0 && contentVO.getState() == 0) {
                int critical = i++;
                String userNick = StringUtils.isNotBlank(contentVO.getUserNick()) ? contentVO.getUserNick() : "";
                if (userNick.length() > 2) {
                    message = "等待" + userNick.substring(1, 3) + "审批";
                } else {
                    message = "等待" + userNick + "审批";
                }
                if (critical == 1) {
                    contentVO.setMessage(message);
                }
            } else if (contentVO.getProcessState() == 1) {
                message = "审批完成";
                if (contentVO.getResult() != null && contentVO.getResult() == 1) {
                    message += " （同意）";
                } else {
                    message += " （拒绝）";
                }
            } else if (contentVO.getState() == 2) {
                message = "已撤回";
            }
            contentVO.setMessage(message);
        }
        convertList(clientApprovalVOS, launchedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getCopied(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentVO> copyApprovalList = copysMapper.getCopiedApprovalList(index, size, orgId, userId, searchKey, flag);
        convertList(clientApprovalVOS, copyApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }


    private void convertList(List<ClientApprovalVO> clientApprovalVOS, List<ApprovalContentVO> approvalList) {
        approvalList.forEach(contentVO -> {
            if (StringUtils.isBlank(contentVO.getUserAvatar())) {
                contentVO.setColor(Colors.generateBeautifulColor(StringUtils.isNotBlank(contentVO.getMobile()) ? contentVO.getMobile() : "", StringUtils.isNotBlank(contentVO.getUserNick()) ? contentVO.getUserNick() : ""));
            }
            ClientApprovalVO clientApprovalVO = new ClientApprovalVO(contentVO);
            clientApprovalVOS.add(clientApprovalVO);
        });
    }
}
