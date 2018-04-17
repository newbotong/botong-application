package com.yunjing.sign.api; 

import com.alibaba.fastjson.JSONObject;
import com.yunjing.sign.beans.param.SignConfigParam;
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

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 

/** 
* SignDetailApi Tester. 
* 
* @author jingwj
* @since <pre>04/16/2018</pre> 
* @version 1.0 
*/ 
@SpringBootTest
@AutoConfigureMockMvc
public class SignDetailApiTest extends BaseTest {

    @Autowired
    private MockMvc mvc; 

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    
    private final MediaType mediaTypeGET = MediaType.APPLICATION_FORM_URLENCODED;
    

    /** 
    * 
    * Method: toSign(@RequestBody SignDetailParam signDetailParam) 
    * 
    */ 
    @Test
    public void testToSign() throws Exception {
        String url = "/sign/detail-out/sign";
        SignDetailParam body = new SignDetailParam();
        body.setAddress("详细地址");
        body.setAddressTitle("地址");
        body.setImgUrls("图片");
        body.setLat("123.222");
        body.setLng("321.555");
        body.setOrgId("123");
        body.setRemark("备注");
        body.setUserId("123");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    }

    /** 
    * 
    * Method: getSignCount(@RequestParam String userId, @RequestParam String orgId) 
    * 
    */ 
    @Test
    public void testGetSignCount() throws Exception {
        String url = "/sign/detail-out/get-count?userId=123&orgId=123";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        getTest(url, mediaTypeGET, null, params);
    }

    /** 
    * 
    * Method: signGroup(@RequestBody UserAndDeptParam userAndDeptParam) 
    * 
    */ 
    @Test
    public void testSignGroup() throws Exception {
        String url = "/sign/detail-out/sign-count";
        UserAndDeptParam body = new UserAndDeptParam();
        body.setDeptIds("123");
        body.setSignDate("2018-4-16");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    }

    /** 
    * 
    * Method: queryMonthInfo(@RequestBody SignDetailParam signDetailParam) 
    * 
    */ 
    @Test
    public void testQueryMonthInfo() throws Exception {
        String url = "/sign/detail-out/query-month";
        SignDetailParam body = new SignDetailParam();
        body.setUserId("6386505038969180166");
        body.setSignDate("2018-3");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    }

} 
