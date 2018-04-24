package com.yunjing.approval.service.impl;

import com.common.mybatis.service.impl.BaseServiceImpl;
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
public class ApprovalProcessServiceImpl extends BaseServiceImpl<ApprovalProcessMapper, ApprovalProcess> implements IApprovalProcessService {

}
