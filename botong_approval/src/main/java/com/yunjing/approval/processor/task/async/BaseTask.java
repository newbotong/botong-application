package com.yunjing.approval.processor.task.async;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * 线程池基类
 * @version 1.0.0
 * @author: zhangx
 * @fate create in 2017/11/20 11:39
 * @description
 **/
public abstract class BaseTask implements Callable, Serializable {

    protected final Logger logger = LogManager.getLogger(this.getClass());

    /*** 线程名称*/
    private String taskName;

    protected void setTaskName(String taskName){
        this.taskName = taskName;
    }


}
