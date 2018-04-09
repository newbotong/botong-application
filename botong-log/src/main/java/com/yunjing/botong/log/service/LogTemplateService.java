package com.yunjing.botong.log.service;

import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.botong.log.vo.LogTemplateItemVo;
import com.yunjing.botong.log.vo.LogTemplateVo;
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
    String createLogTemplate(LogTemplateParam logTemplateParam);

    /**
     * 删除日志模板
     * @param id
     * @return
     */
    boolean deleteLogTemplate(String id);

    /**
     * 查询日志模板列表
     * @param orgId
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageWrapper<LogTemplateItemVo> queryAllLogTemplate(String orgId, int pageNo, int pageSize);


    /**
     * 查询日志模板
     * @param id
     * @return
     */
    LogTemplateVo queryLogTemplate(String id);

    /**
     * 修改日志模板
     * @param logTemplateParam
     * @return
     */
    boolean updateLogTemplate(LogTemplateParam logTemplateParam);
}
