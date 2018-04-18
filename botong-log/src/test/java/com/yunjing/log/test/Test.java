package com.yunjing.log.test;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.yunjing.botong.log.params.ManagerListParam;
import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.mommon.utils.IDUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
public class Test {

    @org.junit.Test
    public void test3() {
        Jedis jedis = new Jedis("192.168.10.48", 10352);
        Map<String, String> map = jedis.hgetAll("botong:org:member");
        Pipeline pipeline = jedis.pipelined();

        Response<List<String>> response = pipeline.hmget("botong:org:member", "6388627626654699520", "6388558948298919939");
        try {
            pipeline.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> list = response.get();
        System.out.println(list);
        System.out.println(map);
    }

    @org.junit.Test
    public void test2() {
        System.out.println(JSON.toJSONString(null));
    }

    @org.junit.Test
    public void test1() {
        ManagerListParam param = new ManagerListParam();
        // param.setAppId("");
        param.setDate("");
        param.setMemberId("");
        param.setOrgId("");
        param.setPageNo(1);
        param.setPageSize(20);
        param.setSubmitType(1);
        System.out.println(JSON.toJSONString(param));
    }


    @org.junit.Test
    public void test() {

        RemindVo remindVo = new RemindVo();
        remindVo.setMemberId(IDUtils.uuid());
        remindVo.setRemindSwitch(1);
        // 日 01:30，周 1-7，月 7号，一次发送：2018-03-23
        remindVo.setCycle("16:30");
        remindVo.setCycleType("ONCE");
        remindVo.setJobTime("2018-3-30 16:30");
        remindVo.setRemindMode(1);
        // remindVo.setAppId("botong-log");
        // 提交周期（1每天 2 每周 3 每月 4 季度 5 年度）
        remindVo.setSubmitType(1);

        Gson gson = new Gson();

        System.out.println(gson.toJson(remindVo));
    }


}
