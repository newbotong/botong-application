package com.yunjing.sign.api; 

import com.alibaba.fastjson.JSONObject;
import com.yunjing.sign.beans.param.SignConfigParam;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 

/** 
* SignConfigApi Tester. 
* 
* @author jingwj
* @since <pre>04/13/2018</pre> 
* @version 1.0 
*/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignConfigApiTest { 

    @Autowired
    private MockMvc mvc; 

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;

    private final MediaType mediaTypeGET = MediaType.APPLICATION_FORM_URLENCODED;


    /**
     * @param url
     * @param jsonContent
     * @param params
     */
    private void postTest(String url, MediaType mediaType, String jsonContent, MultiValueMap<String, String> params){
        try {
            if(StringUtils.isNotBlank(jsonContent)){
                mvc.perform(post(url).contentType(mediaType).content(jsonContent)).andExpect(status().isOk()).andDo(print());
            } else {
                mvc.perform(post(url).contentType(mediaType).params(params)).andExpect(status().isOk()).andDo(print());
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
    * 
    * Method: setting(@RequestBody SignConfigParam signConfigParam) 
    * 
    */ 
    @Test
    public void testSetting() throws Exception {
        String url = "/sign/out/setting";

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
        String url = "/sign/out/get-setting";
        SignConfigParam body = new SignConfigParam();
        body.setOrgId("00000");
        postTest(url, mediaTypeGET, JSONObject.toJSONString(body), null);
    } 


} 
