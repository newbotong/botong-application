package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalProcessMapper;
import com.yunjing.approval.model.entity.ApprovalProcess;
import com.yunjing.approval.service.IApprovalProcessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author roc
 * @date 2018/1/15
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ApprovalProcessServiceImpl extends ServiceImpl<ApprovalProcessMapper, ApprovalProcess> implements IApprovalProcessService {

}
