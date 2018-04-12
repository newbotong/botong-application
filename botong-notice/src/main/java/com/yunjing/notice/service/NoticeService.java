package com.yunjing.notice.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.notice.body.*;
import com.yunjing.notice.entity.NoticeEntity;

import java.io.IOException;
import java.util.Map;

/**
 * 公告Service
 *
 * @author 李双喜
 * @since 2018/03/20/.
 */
public interface NoticeService extends IService<NoticeEntity> {

    /**
     * 新增公告
     *
     * @param noticeBody 新增入参
     * @throws BaseException
     * @throws IOException
     */
    void insertNotice(NoticeBody noticeBody) throws BaseException, IOException;

    /**
     * 逻辑删除公告
     *
     * @param ids 多个公告id
     * @throws BaseException
     */
    void deleteNotice(String ids) throws BaseException;

    /**
     * 分页查询公告
     *
     * @param userId   用户id
     * @param state    是否阅读 0为已读 1为未读
     * @param orgId    企业id
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     * @throws IOException
     */
    Map<String, Object> selectNoticePage(String userId, Integer state, String orgId, Integer pageNo, Integer pageSize) throws BaseException, IOException;


    /**
     * 根据公告id查询已读未读用户接口
     *
     * @param id       公告id
     * @param state    是否阅读 0为已读 1为未读
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     */
    Page<UserInfoBody> selectNoticeUser(String id, Integer state, Integer pageNo, Integer pageSize) throws BaseException, IOException;

    /**
     * 根据公告id查询公告详情接口
     *
     * @param id     公告id
     * @param userId 用户id
     * @return
     * @throws BaseException
     */
    NoticeDetailBody selectNoticeDetail(String id, String userId) throws BaseException,IOException;

    /**
     * web端公告id查询公告详情接口
     *
     * @param id 公告id
     * @return
     * @throws BaseException
     */
    NoticeDetailsBody selectCNoticeDetail(String id) throws BaseException;
}

