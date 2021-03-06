package com.yunjing.botong.log.api;

import com.yunjing.botong.log.service.IRemindService;
import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * <p> 提醒
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/28.
 */
@RestController
@RequestMapping("/log/remind")
public class RemindApi extends BaseController {

    @Autowired
    private IRemindService remindService;

    /**
     * @param remind 提醒对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntityWrapper save(@RequestBody RemindVo remind) {
        BeanFieldValidator.getInstance().validate(remind);
        boolean b = remindService.saveOrUpdate(remind);
        return b ? success() : error();
    }

    /**
     * 查询设置信息
     *
     * @param memberId   用户所有企业的成员id
     * @param submitType 提交类型 日报模版类型（1-日报 2-周报 3-月报）
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntityWrapper info(String memberId, int submitType) {
        return success(remindService.info(memberId, submitType));
    }
}
