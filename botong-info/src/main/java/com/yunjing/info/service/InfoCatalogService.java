package com.yunjing.info.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.dto.CompanyRedisCatalogDto;
import com.yunjing.info.dto.InfoCatalogDto;
import com.yunjing.info.dto.InfoContentDto;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.PageWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 资讯目录接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:37
 */
public interface InfoCatalogService extends IService<InfoCatalog> {

    /**
     * 查询资讯父级目录
     *
     * @param orgId  企业id
     * @param userId 用户id
     * @return
     * @throws BaseException
     */
    Map<String, Object> selectParent(String orgId, String userId) throws BaseException;

    /**
     * 查询资讯父级目录下分页列表
     *
     * @param orgId     企业id
     * @param catalogId 目录id
     * @param userId    用户id
     * @param pageNo    当前页码
     * @param pageSize  每页显示条数
     * @return
     * @throws BaseException
     * @throws IOException
     */
    Map<String, Object> selectParentAll(String orgId, String catalogId, String userId, Integer pageNo, Integer pageSize) throws BaseException, IOException;

    /**
     * 查询企业类目
     *
     * @param orgId 企业类目id
     * @return
     */
    List<CompanyRedisCatalogDto> selectParentCatalog(String orgId);

    /**
     * 新增类目
     *
     * @param orgId
     * @param parentId
     * @param name
     * @return
     * @throws BaseException
     */
    InfoConstant.StateCode insertInfoCategory(String orgId, String parentId, String name) throws BaseException;


    /**
     * 修改分类
     *
     * @param orgId
     * @param parentId
     * @param id
     * @param name
     * @return
     * @throws BaseException
     */
    InfoConstant.StateCode modifyInfoCategory(String orgId, String parentId, String id, String name) throws BaseException;

    /**
     * 删除分类
     *
     * @param orgId
     * @param parentId
     * @param id
     * @return
     * @throws BaseException
     */
    InfoConstant.StateCode deleteInfoCategory(String orgId, String parentId, String id) throws BaseException;


    /**
     * 删除资讯
     *
     * @param orgId
     * @param id
     * @return
     * @throws BaseException
     */
    InfoConstant.StateCode deleteInfoContent(String orgId, String id) throws BaseException;

    /**
     * 显示、隐藏分类
     *
     * @param orgId
     * @param parentId
     * @param id
     * @param displayType 0：否，1：是
     * @return
     * @throws BaseException
     */
    InfoConstant.StateCode displayInfoCategory(String orgId, String parentId, String id, Integer displayType) throws BaseException;


    /**
     * 显示隐藏资讯内容
     *
     * @param orgId
     * @param id
     * @param displayType 0：否，1：是
     * @return
     * @throws BaseException
     */
    InfoConstant.StateCode displayInfoContent(String orgId, String id, Integer displayType) throws BaseException;


    /**
     * 类目排序
     *
     * @param orgId
     * @param parentId
     * @param catalogId1
     * @param catalogId2
     * @return
     * @throws BaseException
     */
    InfoConstant.StateCode updateCatalogSort(String orgId, String parentId, String catalogId1, String catalogId2) throws BaseException;


    /**
     * 资讯排序
     *
     * @param orgId
     * @param catalogId1
     * @param catalogId2
     * @return
     * @throws BaseException
     */
    InfoConstant.StateCode updateInfoSort(String orgId, String catalogId1, String catalogId2) throws BaseException;


    /**
     * web端资讯分页模糊查询
     *
     * @param orgId
     * @param parentId
     * @param catalogId
     * @param title
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BaseException
     */
    PageWrapper<InfoContentDto> selectParentPage(String orgId, String parentId, String catalogId,String title, Integer pageNo, Integer pageSize) throws BaseException;


    /**
     * V1.0资讯数据转换 以及redis处理
     */
    void intoV1DataTransfer();
}
