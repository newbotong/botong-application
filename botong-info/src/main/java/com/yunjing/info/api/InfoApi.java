package com.yunjing.info.api;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 裴志鹏
 * @date 2018/3/20 17:57
 */
@RestController
@RequestMapping("api/info")
public class InfoApi extends BaseController {

    /**
     * 资讯首页列表
     *
     * @param orgId 机构id
     * @return
     */
    @RequestMapping(value = "/getInfoList", method = RequestMethod.POST)
    public ResponseEntityWrapper getInfoList(@RequestParam String orgId) {
        return success();
    }
}
