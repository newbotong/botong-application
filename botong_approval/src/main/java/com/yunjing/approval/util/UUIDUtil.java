package com.yunjing.approval.util;

import com.yunjing.mommon.utils.IDUtils;

import java.util.UUID;

/**
 * 生成系统唯一UUID
 *
 * @version 1.0.0
 * @author: zhangx
 * @date create in 2017/8/31 8:37
 * @description
 */
public class UUIDUtil {

    public static String get() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public static void main(String[] args) {
        int a = 100;
        for (int i = 0; i < a; i++) {
            System.out.println(IDUtils.getID());
        }
    }
}
