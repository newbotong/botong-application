package com.yunjing.info.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.mommon.global.exception.BaseException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 资讯目录接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:37
 */
public interface InfoCatalogService extends IService<InfoCatalog> {
    /**
     * 新增类目
     *
     * @param infoCategoryParam 入参
     * @throws BaseException
     */
    void insertCategory(InfoCategoryParam infoCategoryParam) throws BaseException;


    /**
     * 查询资讯父级目录
     *
     * @param orgId  企业id
     * @param userId 用户id
     * @return
     * @throws BaseException
     */
    Map<String, Object> selectParent(Long orgId, Long userId) throws BaseException;

    /**
     * 查询资讯父级目录下分页列表
     *
     * @param orgId    企业id
     * @param catalogId 类目id
     * @param userId  用户id
     * @param pageNo  当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     */
    Map<String, Object> selectParentAll(Long orgId, Long catalogId, Long userId, Integer pageNo, Integer pageSize) throws BaseException;
}
