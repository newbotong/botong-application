package com.yunjing.info.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.mapper.InfoCatalogMapper;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.mommon.global.exception.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 资讯目录Service实现类
 *
 * @author 李双喜
 * @date 2018/3/30 15:38
 */
@Service
public class InfoCatalogServiceImpl extends ServiceImpl<InfoCatalogMapper, InfoCatalog> implements InfoCatalogService {
    /**
     * 新增类目
     *
     * @param infoCategoryParam 入参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCategory(InfoCategoryParam infoCategoryParam) throws BaseException {

    }

    @Override
    public Map<String, Object> selectParent(Long orgId, Long userId) throws BaseException {
        return null;
    }

    @Override
    public Map<String, Object> selectParentAll(Long orgId, Long catalogId, Long userId) throws BaseException {
        return null;
    }
}
