package com.yunjing.notice;

import com.alibaba.fastjson.JSONObject;
import com.yunjing.notice.body.NoticeBody;
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
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/4/13 14:33
 * @description
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NoticeTest {

    @Autowired
    private MockMvc mvc;

    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;

    /**
     * POST 提交测试
     * @param url
     * @param jsonContent
     * @param params
     */
    public void postTest(String url, String jsonContent, MultiValueMap<String, String> params){
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

    @Test
    public void sendNotice(){
        String url = "/notice/save";

        NoticeBody body = new NoticeBody();
        body.setAuthor("李四");
        body.setContent("小爷我又发公告了");
        body.setCover("http://www.gogao.com/gonggao.jpg");
        body.setDangState(0);
        body.setIssueUserId("6390013882458443776");
        body.setOrgId("ff80808156683550015668c92b4b0058");
        body.setSecrecyState(1);
        body.setTitle("第二次发公告");
        body.setMemberIds("6390013882458443776");
        body.setDepartmentIds("6384295807830462465");

        postTest(url, JSONObject.toJSONString(body), null);
    }

}
