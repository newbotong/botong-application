package com.yunjing.notice.controller;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.body.NoticeDetailsBody;
import com.yunjing.notice.service.NoticeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
     * 新增公告
     *
     * @param body 新增入参
     * @throws BaseException
     * @throws IOException
     */
    @PostMapping("/insert")
    public ResponseEntityWrapper insertNotice(@RequestBody NoticeBody body) throws BaseException, IOException {
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
        if (StringUtils.isEmpty(ids)) {
            throw new BaseException("公告id不能为空");
        }
        noticeService.deleteNotice(ids);
        return success();
    }

    /**
     * Web端查询分页
     *
     * @param userId   用户id
     * @param state    状态
     * @param orgId    企业id
     * @param pageNo   当前页码
     * @param pageSize 每页显示的总条数
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @PostMapping("/page")
    public ResponseEntityWrapper selectNoticePage(@RequestParam String userId, Integer state, @RequestParam String orgId, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws BaseException, IOException {
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
