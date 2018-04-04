package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.ApprovalUserMapper;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.processor.okhttp.OrgMemberService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 刘小鹏
 * @date 2018/03/26
 */
@Service
public class ApprovalUserServiceImpl extends BaseServiceImpl<ApprovalUserMapper, ApprovalUser> implements IApprovalUserService {

    @Autowired
    private OrgMemberService orgMemberService;
    @Autowired
    private IApprovalUserService approvalUserService;

    @Override
    public boolean addUser(String orgId, String userId) throws BaseException {
        String[] passportIds = new String[]{userId};
        ResponseEntityWrapper<List<Map<String, Object>>> memberList = null;
        try {
            memberList = orgMemberService.getMemberList(orgId, passportIds).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<ApprovalUser> list = approvalUserService.selectList(Condition.create().where("org_id={0}", orgId));
        if (memberList.getStatusCode() == StatusCode.SUCCESS.getStatusCode()) {
            List<Map<String, Object>> memberDTOS = memberList.getData();
            List<ApprovalUser> approvalUserList = new ArrayList<>();
            if (list.isEmpty()) {
                for (Map<String, Object> memberDTO : memberDTOS) {
                    ApprovalUser user = new ApprovalUser();
                    user.setId(Long.valueOf(String.valueOf(memberDTO.get("memberId"))));
                    user.setName(String.valueOf(memberDTO.get("name")));
                    user.setMobile(String.valueOf(memberDTO.get("mobile")));
                    user.setPosition(String.valueOf(memberDTO.get("position")));
                    user.setOrgId(Long.valueOf(String.valueOf(memberDTO.get("companyId"))));
                    List<String> deptIds = (List<String>) memberDTO.get("deptIds");
                    String did = "";
                    for (String deptId : deptIds) {
                        if (StringUtils.isNotBlank(deptId)) {
                            did = deptId + ",";
                        }
                    }
                    user.setDeptId(did);
                    List<String> deptNames = (List<String>) memberDTO.get("deptNames");
                    String deptName = "";
                    for (String name : deptNames) {
                        if (StringUtils.isNotBlank(name)) {
                            deptName = name + ",";
                        }
                    }
                    user.setDeptName(deptName);
                    approvalUserList.add(user);
                }
            }
            if (!approvalUserList.isEmpty()) {
                boolean b = insertBatch(approvalUserList);
                if (!b) {
                    throw new BaseException("批量插入企业用户失败");
                }
            }
        } else {
            throw new BaseException("调用企业服务--获取用户数据失败");
        }
        return true;
    }
}
