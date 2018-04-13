package com.yunjing.notice;

import com.alibaba.fastjson.JSONObject;
import com.yunjing.notice.body.NoticeBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/4/13 14:33
 * @description
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NoticeTest extends BaseTest{

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

        postTest(url, MediaType.APPLICATION_JSON_UTF8,JSONObject.toJSONString(body), null);
    }

    @Test
    public void deleteNotice(){
        MultiValueMap params = new LinkedMultiValueMap<>();
        String url = "/notice/delete-batch";
        params.add("ids","004ac8ca877e4ad9b0307e69ca95b9f6");
        postTest(url,MediaType.ALL,null,params);
    }

    @Test
    public void selectNoticePage(){
        MultiValueMap params = new LinkedMultiValueMap<>();
        String url = "/notice/page";
        params.add("userId","547985455656556");
        params.add("state","0");
        params.add("orgId","ff80808156683550015668c92b4b0058");
        params.add("pageNo","1");
        params.add("pageSize","10");
        postTest(url,MediaType.ALL,null,params);
    }

    @Test
    public void selectDetail(){
        MultiValueMap params = new LinkedMultiValueMap<>();
        String url = "/notice/detail";
        params.add("userId","547985455656556");
        params.add("id","014530e2a095422abeb73ad5edf13684");
        postTest(url,MediaType.ALL,null,params);
    }


    @Test
    public void selectNoticeUser(){
        MultiValueMap params = new LinkedMultiValueMap<>();
        String url = "/notice/user";
        params.add("userId","547985455656556");
        params.add("state","0");
        params.add("id","181026927d3a45a2a2acaffb83b449aa");
        params.add("pageNo","1");
        params.add("pageSize","10");
        postTest(url,MediaType.ALL,null,params);
    }















}
