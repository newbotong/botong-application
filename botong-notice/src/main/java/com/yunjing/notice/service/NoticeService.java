package com.yunjing.notice.service;

import com.baomidou.mybatisplus.service.IService;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;

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
     * @return
     */
    void insertNotice(NoticeBody noticeBody) throws BaseException;

}

