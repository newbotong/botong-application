package com.yunjing.notice.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.notice.body.*;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;
import org.yaml.snakeyaml.events.Event;

import java.util.List;
import java.util.Map;

/**
 * 公告
 *
 * @author 李双喜
 * @since 2018/03/20/.
 */
public interface NoticeService extends IService<NoticeEntity> {

    /**
     * 增加公告
     *
     * @param noticeBody
     * @throws BaseException
     */
    void insertNotice(NoticeBody noticeBody) throws BaseException;


    /**
     * 更新已读和未读状态
     * @param userId   用户id
     * @param id       公告id
     * @param state    是否阅读 0为已读 1为未读
     * @throws BaseException
     */
    void updateNoticeState(Long userId,Long id,Integer state) throws BaseException;

    /**
     * 逻辑删除公告
     * @param ids             多个公告id
     * @throws BaseException
     */
    void deleteNotice(String ids) throws BaseException;

    /**
     *
     * 分页查询公告
     * @param userId    用户id
     * @param state     是否阅读 0为已读 1为未读
     * @param pageNo    当前页码
     * @param pageSize  每页显示条数
     * @return
     * @throws BaseException
     */
    Map<String,Object> selectNoticePage(Long userId, Integer state, Integer pageNo, Integer pageSize) throws BaseException;


    /**
     * 根据公告id查询已读未读用户接口
     * @param id     公告id
     * @param state  是否阅读 0为已读 1为未读
     * @return
     * @throws BaseException
     */
    Page<UserInfoBody> selectNoticeUser(Long id,Integer state,Integer pageNo,Integer pageSize) throws BaseException;

    /**
     * 根据公告id查询公告详情接口
     * @param id             公告id
     * @return
     * @throws BaseException
     */
    NoticeDetailBody selectNoticeDetail(Long id,Long userId) throws BaseException;

    /**
     * web端公告id查询公告详情接口
     * @param id 公告id
     */
    NoticeDetailCBody selectCNoticeDetail(Long id) throws BaseException;
}

