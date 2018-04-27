package com.yunjing.sign.controller; 

import com.alibaba.fastjson.JSONObject;
import com.yunjing.sign.api.BaseTest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 

/** 
* SignDailyDetailController Tester. 
* 
* @author jingwj
* @since <pre>04/27/2018</pre> 
* @version 1.0 
*/ 
@SpringBootTest
@AutoConfigureMockMvc
public class SignDailyDetailControllerTest extends BaseTest {

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
    public void testStatistics() throws Exception {
        String url = "/web/sign/daily/statistics";
        UserAndDeptParam body = new UserAndDeptParam();
        body.setMemberId("6391622505735393280");
        body.setSignDate("2018-04");
        body.setPageNo(1);
        body.setPageSize(10);
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    } 

    /** 
    * 
    * Method: setting(@RequestBody SignConfigParam signConfigParam) 
    * 
    */ 
    @Test
    public void testSetting() throws Exception {
        String url = "/web/sign/daily/setting";
        SignConfigParam body = new SignConfigParam();
        body.setAdress("测试地址");
        body.setDistance(100);
        body.setDistanceStatus(1);
        body.setEndTime("10:00");
        body.setLat("10:00");
        body.setLng("10:00");
        body.setOrgId("00000");
        body.setPhotoStatus(1);
        body.setStartTime("08:00");
        body.setTimeStatus(1);
        postTest(url, mediaType, JSONObject.toJSONString(body), null);

    } 

    /** 
    * 
    * Method: getSetting(@RequestParam String orgId) 
    * 
    */ 
    @Test
    public void testGetSetting() throws Exception {
        String url = "/web/sign/daily/get-setting";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("orgId","123");
        getTest(url, mediaTypeGET, null, params);
    } 

    /** 
    * 
    * Method: export(HttpServletResponse response, String userIds, String deptIds, String signDate, String memberId) 
    * 
    */ 
    @Test
    public void testExport() throws Exception {
        String url = "/web/sign/daily/export";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userIds","6384203970528677888");
        params.add("deptIds","6383873980897431552");
        params.add("signDate", "2018-3");
        getTest(url, mediaTypeGET, null, params);
    } 

    /** 
    * 
    * Method: list(@RequestBody SignDetailParam signDetailParam) 
    * 
    */ 
    @Test
    public void testList() throws Exception {
        String url = "/web/sign/daily/list";
        SignDetailParam body = new SignDetailParam();
        body.setUserId("6391622505735393280");
        body.setSignDate("2018-04-28");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    } 

} 
