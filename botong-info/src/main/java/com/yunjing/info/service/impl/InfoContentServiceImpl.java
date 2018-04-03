package com.yunjing.info.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.dto.InfoContentDetailDTO;
import com.yunjing.info.dto.InfoDTO;
import com.yunjing.info.mapper.InfoContentMapper;
import com.yunjing.info.model.InfoContent;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.global.exception.BaseException;
import org.springframework.stereotype.Service;

/**
 * @author 李双喜
 * @date 2018/3/30 15:39
 */
@Service
public class InfoContentServiceImpl extends ServiceImpl<InfoContentMapper,InfoContent> implements InfoContentService {
    @Override
    public InfoContentDetailDTO selectDetail(Long id, Long userId) throws BaseException {
        return null;
    }

    @Override
    public void updateNumber(Long id) throws BaseException {

    }

    @Override
    public void insertInfo(InfoCategoryParam infoCategoryParam) throws BaseException {

    }

    @Override
    public Page<InfoDTO> searchPage(Long orgId, String title) throws BaseException {
        return null;
    }
}
