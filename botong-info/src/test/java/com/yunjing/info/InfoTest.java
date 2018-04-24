package com.yunjing.info;

import com.alibaba.fastjson.JSONObject;
import com.yunjing.info.param.InfoCategoryParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/4/13 14:33
 * @description
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InfoTest extends BaseTest{

    @Test
    public void insertInfo(){
        String url = "/info/insert";

        InfoCategoryParam infoCategoryParam = new InfoCategoryParam();
        infoCategoryParam.setCatalogId("4ddace9bde0c4719a758fcab7ec8dd21");
        infoCategoryParam.setOneCatalogId("dacf195f702b48f390177e8c3f721251");
        infoCategoryParam.setContent("哈哈哈单元测试");
        infoCategoryParam.setDepartmentName("人事部");
        infoCategoryParam.setOrgId("6666666666666");
        infoCategoryParam.setPictureUrl("http://www.baidu.com");
        infoCategoryParam.setTitle("单元测试");

        postTest(url, MediaType.APPLICATION_JSON_UTF8,JSONObject.toJSONString(infoCategoryParam), null);
    }

    @Test
    public void selectParent(){
        MultiValueMap params = new LinkedMultiValueMap<>();
        String url = "/info/parent";
        params.add("orgId","6666666666666");
        params.add("userId","004ac8ca877e4ad9b0307e69ca95b9f6");
        postTest(url,MediaType.ALL,null,params);
    }

    @Test
    public void selectParentAll(){
        MultiValueMap params = new LinkedMultiValueMap<>();
        String url = "/info/parent-all";
        params.add("orgId","6666666666666");
        params.add("catalogId","4ddace9bde0c4719a758fcab7ec8dd21");
        params.add("userId","ff80808156683550015668c92b4b0058");
        params.add("pageNo","1");
        params.add("pageSize","10");
        postTest(url,MediaType.ALL,null,params);
    }

    @Test
    public void selectDetail(){
        MultiValueMap params = new LinkedMultiValueMap<>();
        String url = "/info/detail";
        params.add("userId","547985455656556");
        params.add("id","71ae2e788fd04165be10be5ba11d2316");
        postTest(url,MediaType.ALL,null,params);
    }


    @Test
    public void searchPage(){
        MultiValueMap params = new LinkedMultiValueMap<>();
        String url = "/info/search-page";
        params.add("orgId","6666666666666");
        params.add("title","0");
        params.add("pageNo","1");
        params.add("pageSize","10");
        postTest(url,MediaType.ALL,null,params);
    }















}
