package com.yunjing.info.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.config.InfoConstants;
import com.yunjing.info.dto.InfoCatalogDTO;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.mommon.global.exception.BaseException;

import java.util.List;
import java.util.Map;

/**
 * 资讯目录接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:37
 */
public interface InfoCatalogServiceV2 extends IService<InfoCatalog> {


    /**
     * 新增类目
     * @param orgId
     * @param parentId
     * @param name
     * @return
     * @throws BaseException
     */
    InfoConstants.StateCode insertInfoCategory(Long orgId,Long parentId,String name) throws BaseException;


    /**
     * 修改分类
     * @param orgId
     * @param parentId
     * @param id
     * @param name
     * @return
     * @throws BaseException
     */
    InfoConstants.StateCode modifyInfoCategory(Long orgId,Long parentId,Long id,String name) throws BaseException;

    /**
     * 删除分类
     * @param orgId
     * @param parentId
     * @param id
     * @return
     * @throws BaseException
     */
    InfoConstants.StateCode deleteInfoCategory(Long orgId,Long parentId,Long id) throws BaseException;


    /**
     * 删除资讯
     * @param orgId
     * @param id
     * @return
     * @throws BaseException
     */
    InfoConstants.StateCode deleteInfoContent(Long orgId,Long id) throws BaseException;

    /**
     * 显示、隐藏分类
     * @param orgId
     * @param parentId
     * @param id
     * @param displayType 0：否，1：是
     * @return
     * @throws BaseException
     */
    InfoConstants.StateCode displayInfoCategory(Long orgId,Long parentId,Long id,Integer displayType) throws BaseException;


    /**
     * 显示隐藏资讯内容
     * @param orgId
     * @param id
     * @param displayType  0：否，1：是
     * @return
     * @throws BaseException
     */
    InfoConstants.StateCode displayInfoContent(Long orgId,Long id,Integer displayType) throws BaseException;


    /**
     * 获取一级下的所有分类
     * @param orgId
     * @return
     */
    List<InfoCatalogDTO> getInfoCatalogList(Long orgId) throws BaseException;

}
