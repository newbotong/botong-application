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
* SignConfigDailyApi Tester. 
* 
* @author jingwj
* @since <pre>04/16/2018</pre> 
* @version 1.0 
*/ 
@SpringBootTest
@AutoConfigureMockMvc
public class SignConfigDailyApiTest extends BaseTest {

    @Autowired
    private MockMvc mvc; 

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;

    private final MediaType mediaTypeGET = MediaType.APPLICATION_FORM_URLENCODED;


    /**
     *
     * Method: setting(@RequestBody SignConfigParam signConfigParam)
     *
     */
    @Test
    public void testSetting() throws Exception {
        String url = "/sign/daily/setting";

        SignConfigParam body = new SignConfigParam();
        body.setAdress("测试地址1");
        body.setDistance(10);
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
        String url = "/sign/daily/get-setting?orgId=0000";
        getTest(url, mediaTypeGET, null, null);
    }


} 
