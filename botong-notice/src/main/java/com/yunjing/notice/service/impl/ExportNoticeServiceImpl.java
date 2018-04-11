package com.yunjing.notice.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.notice.entity.ExportNoticeLogEntity;
import com.yunjing.notice.mapper.ExportNoticeLogMapper;
import com.yunjing.notice.service.ExportNoticeLogService;
import org.springframework.stereotype.Service;

/**
 * @author 李双喜
 * @date 2018/4/11 10:08
 */
@Service
public class ExportNoticeServiceImpl extends ServiceImpl<ExportNoticeLogMapper, ExportNoticeLogEntity> implements ExportNoticeLogService {
}
