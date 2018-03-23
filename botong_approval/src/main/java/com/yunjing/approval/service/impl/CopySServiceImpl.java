package com.yunjing.approval.service.impl;

import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.CopySMapper;
import com.yunjing.approval.model.entity.CopyS;
import com.yunjing.approval.service.ICopySService;
import org.springframework.stereotype.Service;

/**
 * @author liuxiaopeng
 * @date 2018/1/15
 */
@Service
public class CopySServiceImpl extends BaseServiceImpl<CopySMapper, CopyS> implements ICopySService {
}
