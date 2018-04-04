package com.yunjing.approval.service.impl;

import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.CopysMapper;
import com.yunjing.approval.model.entity.Copys;
import com.yunjing.approval.service.ICopysService;
import org.springframework.stereotype.Service;

/**
 * @author liuxiaopeng
 * @date 2018/1/15
 */
@Service
public class CopysServiceImpl extends BaseServiceImpl<CopysMapper, Copys> implements ICopysService {
}
