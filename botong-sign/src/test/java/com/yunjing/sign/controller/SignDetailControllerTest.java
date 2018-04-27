package com.yunjing.sign.controller; 

import com.alibaba.fastjson.JSONObject;
import com.yunjing.sign.api.BaseTest;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 

/** 
* SignDetailController Tester. 
* 
* @author jingwj
* @since <pre>04/27/2018</pre> 
* @version 1.0 
*/ 
@SpringBootTest
@AutoConfigureMockMvc
public class SignDetailControllerTest extends BaseTest{

    @Autowired
    private MockMvc mvc; 

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    
    private final MediaType mediaTypeGET = MediaType.APPLICATION_FORM_URLENCODED;
    

    /** 
    * 
    * Method: statistics(@RequestBody UserAndDeptParam userAndDeptParam) 
    * 
    */ 
    @Test
    public void testStatisticsUserAndDeptParam() throws Exception {
        String url = "/web/sign/out/statistics";
        UserAndDeptParam body = new UserAndDeptParam();
        body.setMemberId("6391622505735393280");
        body.setSignDate("2018-04");
        body.setPageNo(1);
        body.setPageSize(10);
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    } 

    /** 
    * 
    * Method: export(HttpServletResponse response, String userIds, String deptIds, String signDate, String memberId) 
    * 
    */ 
    @Test
    public void testExport() throws Exception {
        String url = "/web/sign/out/export";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userIds","6384203970528677888");
        params.add("deptIds","6383873980897431552");
        params.add("signDate", "2018-3");
        getTest(url, mediaTypeGET, null, params);
    } 

    /** 
    * 
    * Method: statistics(@RequestBody SignDetailParam signDetailParam) 
    * 
    */ 
    @Test
    public void testListSignDetailParam() throws Exception {
        String url = "/web/sign/out/list";
        SignDetailParam  body = new SignDetailParam();
        body.setUserId("6391622505735393280");
        body.setSignDate("2018-04-28");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);

    }
} 
