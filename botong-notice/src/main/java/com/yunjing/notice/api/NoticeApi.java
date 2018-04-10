package com.yunjing.notice.api;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.body.NoticeDetailBody;
import com.yunjing.notice.body.UserInfoBody;
import com.yunjing.notice.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * <p> 公告api接口
 * </p>
 *
 * @author 李双喜
 * @since 2018/3/20.
 */
@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeApi extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 新增公告接口
     *
     * @param body 公告实体类
     * @throws BaseException
     */
    @PostMapping("/save")
    public ResponseEntityWrapper insertNotice(@RequestBody NoticeBody body) throws BaseException {
        BeanFieldValidator.getInstance().ignore().validate(body);
        noticeService.insertNotice(body);
        return success();
    }

    /**
     * 更新已读和未读状态
     *
     * @param userId 用户id
     * @param id     公告id
     * @param state  是否阅读 0为已读 1为未读
     * @throws BaseException
     */
    @PostMapping("/update")
    public ResponseEntityWrapper updateNoticeState(@RequestParam String userId, @RequestParam String id, @RequestParam Integer state) throws BaseException {
        noticeService.updateNoticeState(userId, id, state);
        return success();
    }

    /**
     * 逻辑删除公告
     *
     * @param ids 多个公告id，用逗号隔开
     * @throws BaseException
     */
    @PostMapping("/delete-batch")
    public ResponseEntityWrapper deleteNotice(@RequestParam String ids) throws BaseException {
        noticeService.deleteNotice(ids);
        return success();
    }

    /**
     * 查询公告列表接口
     *
     * @param userId   用户id
     * @param state    阅读状态   0已读 1未读 2为管理员
     * @param pageNo   当前页码
     * @param pageSize 每页显示的条数
     * @return
     * @throws BaseException
     */
    @PostMapping("/page")
    public ResponseEntityWrapper selectNoticePage(@RequestParam String userId, Integer state, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws BaseException {
        Map<String, Object> map = noticeService.selectNoticePage(userId, state, pageNo, pageSize);
        return success(map);
    }

    /**
     * 根据公告id查询公告详情接口
     *
     * @param id 公告id
     * @return
     * @throws BaseException
     */
    @PostMapping("/detail")
    public ResponseEntityWrapper selectNoticeDetail(@RequestParam String id, @RequestParam String userId) throws BaseException {
        NoticeDetailBody noticeDetailBody = noticeService.selectNoticeDetail(id, userId);
        return success(noticeDetailBody);
    }

    /**
     * 根据公告id查询已读未读用户接口
     *
     * @param id    公告id
     * @param state 是否阅读 0为已读 1为未读
     * @return
     * @throws BaseException
     */
    @PostMapping("/user")
    public ResponseEntityWrapper selectNoticeUser(@RequestParam String id, @RequestParam Integer state, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws BaseException {
        Page<UserInfoBody> page = noticeService.selectNoticeUser(id, state, pageNo, pageSize);
        return success(page);
    }
}
