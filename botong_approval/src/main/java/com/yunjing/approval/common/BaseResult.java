package com.yunjing.approval.common;


/**
 * @author liuxiaopeng
 * @date 2018/01/23
 */
public class BaseResult<T> {

    private T data;

    private String code;

    private String msg;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
