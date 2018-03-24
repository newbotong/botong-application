package com.yunjing.approval.service.impl;

import com.common.mybatis.page.Page;
import com.yunjing.approval.common.Colors;
import com.yunjing.approval.common.DateUtil;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.ApprovalProcessMapper;
import com.yunjing.approval.dao.mapper.ModelLMapper;
import com.yunjing.approval.model.dto.ApprovalContentVO;
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
    public Page<ClientApprovalVO> getWaited(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current,size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentVO> waitedMeApprovalList = approvalProcessMapper.getWaitedMeApprovalList(index, size, orgId, userId, searchKey, flag);
        waitedMeApprovalList.forEach(contentVO ->{
            if(StringUtils.isBlank(contentVO.getUserAvatar())){
                contentVO.setMobile("18291495379");
                contentVO.setUserNick("刘小鹏");
                contentVO.setColor(Colors.generateBeautifulColor(contentVO.getMobile(),contentVO.getUserNick()));
            }
            ClientApprovalVO clientApprovalVO = new ClientApprovalVO(contentVO);
            clientApprovalVOS.add(clientApprovalVO);
        });
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }
}
