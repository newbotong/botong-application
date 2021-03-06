package com.yunjing.notice.api;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.body.NoticeDetailBody;
import com.yunjing.notice.body.UserInfoBody;
import com.yunjing.notice.processor.okhttp.AuthorityService;
import com.yunjing.notice.processor.okhttp.DangService;
import com.yunjing.notice.processor.okhttp.InformService;
import com.yunjing.notice.service.ExportNoticeService;
import com.yunjing.notice.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @Autowired
    private ExportNoticeService exportNoticeService;

    /**
     * 新增公告
     *
     * @param body 新增入参
     * @throws BaseException
     * @throws IOException
     */
    @PostMapping("/save")
    public ResponseEntityWrapper insertNotice(@RequestBody NoticeBody body) throws BaseException, IOException {
        BeanFieldValidator.getInstance().ignore().validate(body);
        noticeService.insertNotice(body);
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
        if (StringUtils.isEmpty(ids)) {
            throw new BaseException("公告id不能为空");
        }
        noticeService.deleteNotice(ids);
        return success();
    }

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
    @PostMapping("/page")
    public ResponseEntityWrapper selectNoticePage(@RequestParam String userId, Integer state, @RequestParam String orgId, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws BaseException, IOException {
        Map<String, Object> map = noticeService.selectNoticePage(userId, state, orgId, pageNo, pageSize);
        return success(map);
    }

    /**
     * 根据公告id查询公告详情接口
     *
     * @param id     公告id
     * @param userId 成员id
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @PostMapping("/detail")
    public ResponseEntityWrapper selectNoticeDetail(@RequestParam String id, @RequestParam String userId) throws BaseException, IOException {
        if (StringUtils.isAnyBlank(id, userId)) {
            throw new BaseException("参数不能为空");
        }
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
    public ResponseEntityWrapper selectNoticeUser(@RequestParam String id, @RequestParam Integer state, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws BaseException, IOException {
        if (null == id && null == state) {
            throw new BaseException("参数错误");
        }
        Page<UserInfoBody> page = noticeService.selectNoticeUser(id, state, pageNo, pageSize);
        return success(page);
    }


    /**
     * 查询用户权限
     *
     * @param userId 成员id
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @PostMapping("/authority")
    public ResponseEntityWrapper selectAuthority(@RequestParam String userId) throws BaseException,IOException {
        Boolean result = noticeService.selectAuthority(userId);
        return success(result);
    }

    /**
     * 数据导入
     */
    @PostMapping("/import")
    public ResponseEntityWrapper importData() {
        exportNoticeService.importData();
        return success();
    }

}
