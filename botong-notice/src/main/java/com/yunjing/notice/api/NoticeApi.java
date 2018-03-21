package com.yunjing.notice.api;

import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.service.NoticeService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * <p> 公告api接口
 * </p>
 *
 * @author 李双喜
 * @since 2018/3/20.
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeApi extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 新增公告接口
     *
     * @param body
     * @return
     */
    @PostMapping("/insertNotice")
    public ResponseEntityWrapper insertNotice(@RequestBody NoticeBody body) throws BaseException {
        noticeService.insertNotice(body);
        return success();
    }

    /**
     * 修改已读未读状态接口
     * @param body
     * @return
     */
    @PostMapping("/updateNoticeState")
    public ResponseEntityWrapper updateNoticeState(@RequestBody NoticeBody body) {

        return success();
    }
    /**
     * 删除公告接口
     */
    @PostMapping("/deleteNotice")
    public ResponseEntityWrapper deleteNotice(@RequestBody NoticeBody body) {

        return success();
    }
    /**
     *
     * 查询公告列表接口
     *
     */
    @PostMapping("/selectNoticePage")
    public ResponseEntityWrapper selectNoticePage(@RequestBody NoticeBody body) {

        return success();
    }

    /**
     * 根据公告id查询公告详情接口
     */
    @PostMapping("/selectNoticeDetail")
    public ResponseEntityWrapper selectNoticeDetail(@RequestBody NoticeBody body) {

        return success();
    }
    /**
     * 根据公告id查询公告详情接口
     */
    @PostMapping("/selectNoticeUser")
    public ResponseEntityWrapper selectNoticeUser(@RequestBody NoticeBody body) {

        return success();
    }
}
