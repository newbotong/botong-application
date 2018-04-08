package com.yunjing.info.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.yunjing.info.dto.InfoContentDetailDTO;
import com.yunjing.info.dto.InfoDTO;
import com.yunjing.info.model.InfoContent;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.mommon.global.exception.BaseException;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * 资讯内容接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:39
 */
public interface InfoContentService extends IService<InfoContent> {
    /**
     * 查询资讯详情接口
     *
     * @param id     资讯id
     * @param userId 用户id
     * @return
     * @throws BaseException
     * @throws IOException
     */
    InfoContentDetailDTO selectDetail(Long id, Long userId) throws BaseException, IOException;

    /**
     * 更新阅读数量接口
     *
     * @param id 资讯id
     * @return
     * @throws BaseException
     */
    void updateNumber(Long id) throws BaseException;

    /**
     * 新增资讯接口
     *
     * @param infoCategoryParam 入参对象
     * @return
     * @throws BaseException
     */
    void insertInfo(InfoCategoryParam infoCategoryParam) throws BaseException;

    /**
     * 模糊查询资讯
     *
     * @param orgId    企业
     * @param title    标题
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     */
    Page<InfoDTO> searchPage(Long orgId, String title, Integer pageNo, Integer pageSize);
}
