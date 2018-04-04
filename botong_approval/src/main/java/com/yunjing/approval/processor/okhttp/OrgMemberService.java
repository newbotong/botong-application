package com.yunjing.approval.processor.okhttp;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Call;

/**
 * @author 刘小鹏
 * @date 2018/3/26
 */
public interface OrgMemberService {
    /**
     * 获取企业用户信息
     *
     * @param companyId   公司主键
     * @param passportIds 成员id集合,逗号隔开
     * @return ResponseEntityWrapper
     */
    @RequestMapping(value = "/rpc/member/get-members-list", method = RequestMethod.POST)
    Call<ResponseEntityWrapper> getMemberList(@RequestParam("companyId") String companyId, @RequestParam("passportIds") String[] passportIds);
}
