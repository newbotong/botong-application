package com.yunjing.botong.log.controller; 

import com.alibaba.fastjson.JSONObject;
import com.yunjing.botong.log.LogApplication;
import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.params.SearchParam;
import com.yunjing.log.test.BaseTest;
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
* LogSearchController Tester. 
* 
* @author jingwj
* @since <pre>04/27/2018</pre> 
* @version 1.0 
*/ 
@SpringBootTest(classes = LogApplication.class)
@AutoConfigureMockMvc
public class LogSearchControllerTest extends BaseTest {

    @Autowired
    private MockMvc mvc; 

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    
    private final MediaType mediaTypeGET = MediaType.APPLICATION_FORM_URLENCODED;
    

    /** 
    * 
    * Method: receviedPage(@RequestBody SearchParam searchParam) 
    * 
    */ 
    @Test
    public void testReceviedPage() throws Exception {
        String url = "/web/log/manage/find-page";
        SearchParam body = new SearchParam();
        body.setOrgId("6384295807801102336");
        body.setSubmitType(0);
        body.setMemberId("6391622505735393288");
        body.setPageSize(10);
        body.setPageNo(1);
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    } 

    /** 
    * 
    * Method: delete(@RequestBody SearchParam searchParam) 
    * 
    */ 
    @Test
    public void testDelete() throws Exception {
        String url = "/web/log/manage/delete";
        SearchParam body = new SearchParam();
        body.setLogIds(new String[]{"10566c5db8d046e599252caf4c134d32"});
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    } 

    /** 
    * 
    * Method: export(HttpServletResponse response, @RequestParam(value = "submitType", required = false) String submitType, @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "orgId") String orgId, @RequestParam(value = "memberId", required = false) String memberId, @RequestParam(value = "userIds", required = false) String[] userIds, @RequestParam(value = "deptIds", required = false) String[] deptIds) 
    * 
    */ 
    @Test
    public void testExport() throws Exception {
        String url = "/web/log/manage/export";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("orgId","6384295807801102336");
        params.add("memberId","6386783254216708096");
        params.add("submitType", "0");
        getTest(url, mediaTypeGET, null, params);
    } 
}
