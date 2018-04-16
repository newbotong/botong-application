package com.yunjing.info;

import org.apache.commons.lang.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 单元测试基类
 * @version 1.0.0
 * @author: zhangx
 * @date 2018/3/7 9:55
 * @description
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseTest {

    @Autowired
    private MockMvc mvc;

    public void postTest(String url, MediaType mediaType, String jsonContent, MultiValueMap<String, String> params){
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

    public void putTest(String url, MediaType mediaType, String jsonContent, MultiValueMap<String, String> params){
        try {
            if(StringUtils.isNotBlank(jsonContent)){
                mvc.perform(put(url).contentType(mediaType).content(jsonContent)).andExpect(status().isOk()).andDo(print());
            } else {
                mvc.perform(put(url).contentType(mediaType).params(params)).andExpect(status().isOk()).andDo(print());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTest(String url, MediaType mediaType, String jsonContent, MultiValueMap<String, String> params){
        try {
            if(StringUtils.isNotBlank(jsonContent)){
                mvc.perform(get(url).contentType(mediaType).content(jsonContent)).andExpect(status().isOk()).andDo(print());
            } else {
                mvc.perform(get(url).contentType(mediaType).params(params)).andExpect(status().isOk()).andDo(print());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTest(String url, MediaType mediaType, String jsonContent, MultiValueMap<String, String> params){
        try {
            if(StringUtils.isNotBlank(jsonContent)){
                mvc.perform(delete(url).contentType(mediaType).content(jsonContent)).andExpect(status().isOk()).andDo(print());
            } else {
                mvc.perform(delete(url).contentType(mediaType).params(params)).andExpect(status().isOk()).andDo(print());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
