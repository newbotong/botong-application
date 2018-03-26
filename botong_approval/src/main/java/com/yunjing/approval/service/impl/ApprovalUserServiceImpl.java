package com.yunjing.approval.service.impl;

import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalUserMapper;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.processor.feign.OrgUserFeign;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.ListUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/03/26
 */
@Service
public class ApprovalUserServiceImpl extends BaseServiceImpl<ApprovalUserMapper, ApprovalUser> implements IApprovalUserService {

    @Autowired
    private OrgUserFeign orgUserFeign;

    @Override
    public boolean addUser(Long orgId) throws BaseException {
        ResponseEntityWrapper orgUser = orgUserFeign.getOrgUser(orgId);
        if (orgUser.getStatusCode() == StatusCode.SUCCESS.getStatusCode()) {
            Object data = orgUser.getData();
            // TODO rpc调用企业服务获取用户信息
            List<ApprovalUser> approvalUserList = new ArrayList<>();
            boolean b = this.insertBatch(approvalUserList);
            if(!b){
                throw new BaseException("批量插入企业用户失败");
            }
        } else {
            throw new BaseException("调用企业服务--获取用户数据失败");
        }
        return true;
    }
}
