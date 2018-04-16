package com.yunjing.notice.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.notice.entity.ExportNoticeEntity;

/**
 * @author 李双喜
 * @date 2018/4/11 10:00
 */

public interface ExportNoticeService extends IService<ExportNoticeEntity> {
    /**
     * 导入数据
     */
    void importData();
}
