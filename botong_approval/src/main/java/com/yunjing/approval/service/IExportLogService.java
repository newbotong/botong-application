package com.yunjing.approval.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.ExportLog;
import com.yunjing.approval.model.vo.LogPageVO;

/**
 * 导出记录--业务接口
 *
 * @author liuxiaopeng
 * @date 2018/01/15
 */
public interface IExportLogService extends IBaseService<ExportLog> {

    /**
     * 获取所有导出记录
     *
     * @param page 分页对象
     * @param orgId 企业主键
     * @return
     */
    LogPageVO findExportLogPage(Page page, String orgId);
}