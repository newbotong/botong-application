package com.yunjing.botong.log.api;

import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
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

    /**
     * @param remind 提醒对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntityWrapper save(RemindVo remind) {
        BeanFieldValidator.getInstance().validate(remind);
        return success();
    }

    /**
     * @param memberId 用户所有企业的成员id
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntityWrapper info(long memberId) {
        return success();
    }
}
