package com.yunjing.approval.service.impl;

import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalAttrMapper;
import com.yunjing.approval.model.entity.ApprovalAttr;
import com.yunjing.approval.service.IApprovalAttrService;
import org.springframework.stereotype.Service;

/**
 * @author liuxiaopeng
 * @date 2018/1/15
 */
@Service
public class ApprovalAttrServiceImpl extends BaseServiceImpl<ApprovalAttrMapper, ApprovalAttr> implements IApprovalAttrService {
}
