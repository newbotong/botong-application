package com.yunjing.botong.log.service;

import com.baomidou.mybatisplus.service.IService;
import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.vo.LogDetailVO;

import java.util.List;

/**
 * <p>
 * 签到配置 服务类
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
public interface ILogSearchService  {



    /**
     * 查询我发送的日志列表
     * @return
     */
    PageWrapper<LogDetailVO> receivePage(ReceviedParam receviedParam);

}
