package com.yunjing.approval.service.impl;

import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalUserMapper;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.vo.MemberInfo;
import com.yunjing.approval.model.vo.OrgMemberVo;
import com.yunjing.approval.processor.okhttp.AppCenterService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.mommon.global.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/03/26
 */
@Service
public class ApprovalUserServiceImpl extends BaseServiceImpl<ApprovalUserMapper, ApprovalUser> implements IApprovalUserService {

    @Autowired
    private IApprovalUserService approvalUserService;

    @Autowired
    private AppCenterService appCenterService;

    @Override
    public boolean addUser(String orgId) throws BaseException {
//        List<OrgMemberVo> allOrgMember = appCenterService.findAllOrgMember(orgId, true);
      //  deptIds:383879319994765312,6384203970528677888
        String[] deptIds = new String[]{"6383873980897431552","6384203970528677888"};
        List<MemberInfo> subList = appCenterService.findSubList(deptIds, null, true);
        return true;
    }
}
