package com.yunjing.approval.service.impl;


import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.PushLogMapper;
import com.yunjing.approval.model.entity.PushLog;
import com.yunjing.approval.service.IPushLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 推送记录实现类
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Service
public class PushLogServiceImpl extends BaseServiceImpl<PushLogMapper, PushLog> implements IPushLogService {

}



