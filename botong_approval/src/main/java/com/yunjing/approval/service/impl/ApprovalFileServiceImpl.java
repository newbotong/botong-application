package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalFileMapper;
import com.yunjing.approval.model.entity.ApprovalFile;
import com.yunjing.approval.service.IApprovalFileService;
import org.springframework.stereotype.Service;

/**
 * @author liuxiaopeng
 * @date 2018/1/15
 */
@Service
public class ApprovalFileServiceImpl extends ServiceImpl<ApprovalFileMapper, ApprovalFile> implements IApprovalFileService {
}
