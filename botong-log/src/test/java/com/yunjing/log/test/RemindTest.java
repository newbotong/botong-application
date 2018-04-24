package com.yunjing.log.test;

import com.alibaba.fastjson.JSONObject;
import com.yunjing.botong.log.LogApplication;
import com.yunjing.botong.log.config.CycleType;
import com.yunjing.botong.log.vo.RemindVo;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * <p>
 * <p> 日志单元测试
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/20.
 */
@SpringBootTest(classes = LogApplication.class)
@AutoConfigureMockMvc
public class RemindTest extends BaseTest {

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;

    private final MediaType mediaTypeGET = MediaType.APPLICATION_FORM_URLENCODED;

    /**
     * 保存提醒
     */
    @Test
    public void test() {
        String url = "/log/remind/save";
        RemindVo remindVo = new RemindVo();
        remindVo.setCycle("*");
        remindVo.setCycleType(CycleType.WEEK.toString());
        remindVo.setSubmitType(1);
        remindVo.setJobTime("15:30");
        remindVo.setOrgId("xxxx");
        remindVo.setMemberId("xxxx");
        remindVo.setRemindMode(1);
        remindVo.setIsManager(0);
        postTest(url, mediaType, JSONObject.toJSONString(remindVo), null);
    }

    /**
     * 获取保存的提醒详情
     */
    @Test
    public void test1() {
        String url = "/log/remind/info?memberId=xxx&submitType=1";
        getTest(url, mediaTypeGET, null, null);
    }

    /**
     * 日志报表
     */
    @Test
    public void test2() {
        String url = "/log/report/list";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("memberId", "xxxx");
        params.add("orgId", "xxxx");
        params.add("pageNo", "1");
        params.add("pageSize", "20");
        getTest(url, mediaTypeGET, null, params);
    }

    /**
     * 管理-已提交列表
     */
    @Test
    public void test3() {
        String url = "/log/report/manager-submit-list";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("memberId", "xxxx");
        params.add("orgId", "xxxx");
        params.add("submitType", "1");
        params.add("date", "2018-04-15");
        params.add("pageNo", "1");
        params.add("pageSize", "20");
        postTest(url, mediaType, JSONObject.toJSONString(params), null);
    }

    /**
     * 管理-未提交列表
     */
    @Test
    public void test4() {
        String url = "/log/report/manager-unsubmit-list";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("memberId", "xxxx");
        params.add("orgId", "xxxx");
        params.add("submitType", "1");
        params.add("date", "2018-04-15");
        params.add("pageNo", "1");
        params.add("pageSize", "20");
        postTest(url, mediaType, JSONObject.toJSONString(params), null);
    }
}
