package com.yunjing.notice.controller;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.body.NoticeDetailsBody;
import com.yunjing.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 公告controller
 *
 * @author 李双喜
 * @date 2018/3/21 17:40
 */
@RestController
@RequestMapping("/web/notice")
public class NoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 新增公告接口
     *
     * @param body 入参实体
     * @return
     */
    @PostMapping("/insert")
    public ResponseEntityWrapper insertNotice(@RequestBody NoticeBody body) throws BaseException {
        noticeService.insertNotice(body);
        return success();
    }

    /**
     * 删除公告接口
     *
     * @param ids 公告id
     * @return
     * @throws BaseException
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper deleteNotice(@RequestParam String ids) throws BaseException {
        noticeService.deleteNotice(ids);
        return success();
    }

    /**
     * 查询web端公告分页
     *
     * @param userId   用户id
     * @param pageNo   当前页码
     * @param pageSize 每页显示的总条数
     * @return
     */
    @PostMapping("/page")
    public ResponseEntityWrapper selectNoticePage(@RequestParam String userId, Integer state, @RequestParam String orgId, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws BaseException {
        Map<String, Object> map = noticeService.selectNoticePage(userId, state, orgId, pageNo, pageSize);
        return success(map);
    }

    /**
     * web根据id查询公告
     *
     * @param id
     * @return
     * @throws BaseException
     */
    @PostMapping("/detail")
    public ResponseEntityWrapper selectNoticeDetail(@RequestParam String id) throws BaseException {
        NoticeDetailsBody noticeDetailsBody = noticeService.selectCNoticeDetail(id);
        return success(noticeDetailsBody);
    }
}
