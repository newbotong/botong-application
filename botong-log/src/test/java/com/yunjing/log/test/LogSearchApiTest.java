package com.yunjing.log.test;

import com.alibaba.fastjson.JSONObject;
import com.yunjing.botong.log.LogApplication;
import com.yunjing.botong.log.params.ReceviedParam;
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
* LogSearchApi Tester. 
* 
* @author jingwj
* @since <pre>04/17/2018</pre> 
* @version 1.0 
*/ 
@SpringBootTest(classes = LogApplication.class)
@AutoConfigureMockMvc
public class LogSearchApiTest extends BaseTest{

    @Autowired
    private MockMvc mvc; 

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    
    private final MediaType mediaTypeGET = MediaType.APPLICATION_FORM_URLENCODED;
    

    /** 
    * 
    * Method: receviedPage(@RequestBody ReceviedParam receviedParam) 
    * 
    */ 
    @Test
    public void testReceviedPage() throws Exception {
        String url = "/log/search/recevied-page";
        ReceviedParam body = new ReceviedParam();
        body.setUserId("6390751994696437763");
        body.setOrgId("6384295807801102336");
        body.setPageSize(10);
        body.setPageNo(1);
        postTest(url, mediaType, JSONObject.toJSONString(body), null);

    } 

    /** 
    * 
    * Method: sendPage(@RequestBody ReceviedParam receviedParam) 
    * 
    */ 
    @Test
    public void testSendPage() throws Exception {
        String url = "/log/search/send-page";
        ReceviedParam body = new ReceviedParam();
        body.setUserId("6390751994696437763");
        body.setOrgId("6384295807801102336");
        body.setPageSize(10);
        body.setPageNo(1);
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    } 

    /** 
    * 
    * Method: read(@RequestBody ReceviedParam receviedParam) 
    * 
    */ 
    @Test
    public void testRead() throws Exception {
        String url = "/log/search/read";
        ReceviedParam body = new ReceviedParam();
        body.setUserId("6390751994696437763");
        body.setLogId("61dacd4be63e4937bf96551a6f711212");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    } 

    /** 
    * 
    * Method: readAll(@RequestBody ReceviedParam receviedParam) 
    * 
    */ 
    @Test
    public void testReadAll() throws Exception {
        String url = "/log/search/read-all";
        ReceviedParam body = new ReceviedParam();
        body.setUserId("6390751994696437763");
        body.setOrgId("123");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    } 

    /** 
    * 
    * Method: delete(@RequestBody ReceviedParam receviedParam) 
    * 
    */ 
    @Test
    public void testDelete() throws Exception {
        String url = "/log/search/delete";
        ReceviedParam body = new ReceviedParam();
        body.setUserId("6390751994696437763");
        body.setLogId("a1889cb2e4c646e38a9533e411e02dd0");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);

    } 

    /** 
    * 
    * Method: get(@RequestBody ReceviedParam receviedParam) 
    * 
    */ 
    @Test
    public void testGet() throws Exception {
        String url = "/log/search/get";
        ReceviedParam body = new ReceviedParam();
        body.setUserId("6390751994696437763");
        body.setLogId("a1889cb2e4c646e38a9533e411e02dd0");
        postTest(url, mediaType, JSONObject.toJSONString(body), null);
    }

    /**
     *
     * Method: get(@RequestBody ReceviedParam receviedParam)
     *
     */
    @Test
    public void testQuery() throws Exception {
        String url = "/log/template/query";

        MultiValueMap param = new LinkedMultiValueMap();
        param.add("id", "ff80808156683550015668c92b510059");


        getTest(url, mediaType, null, param);
    }

}
