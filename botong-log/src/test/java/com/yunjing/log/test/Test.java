package com.yunjing.log.test;

import com.google.gson.Gson;
import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.mommon.utils.IDUtils;

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
    public void test(){

        RemindVo remindVo = new RemindVo();
        remindVo.setUserId(IDUtils.getID());
        remindVo.setRemindSwitch(1);
        // 日 01:30，周 1-7，月 7号，一次发送：2018-03-23
        remindVo.setCycle("16:30");
        remindVo.setCycleType("day");
        remindVo.setJobTime("16:30");
        remindVo.setRemindMode("dang");
        // 提交周期（1每天 2 每周 3 每月 4 季度 5 年度）
        remindVo.setSubmitType(1);

        Gson gson = new Gson();

        System.out.println(gson.toJson(remindVo));
    }
}
