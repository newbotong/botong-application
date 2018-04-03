package com.yunjing.botong.log.service;

import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.botong.log.vo.LogTemplateItem;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;

/**
 * 日志模板服务
 *
 * @author 王开亮
 * @date 2018/4/2 10:48
 */
public interface LogTemplateService {

    /**
     * 创建日志模板
     * @param logTemplateParam  日志模板参数
     * @return
     */
    long createLogTemplate(LogTemplateParam logTemplateParam);

    /**
     * 删除日志模板
     * @param id
     * @return
     */
    boolean deleteLogTemplate(long id);

    /**
     * 查询日志模板列表
     * @param orgId
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageWrapper<LogTemplateItem> queryAllLogTemplate(long orgId, int pageNo, int pageSize);

}
