package com.yunjing.botong.log.service;

import com.common.mybatis.service.IBaseService;
import com.yunjing.botong.log.entity.RemindEntity;
import com.yunjing.botong.log.vo.RemindVo;

/**
 * <p>
 * 提醒 服务类
 * </p>
 *
 * @author tao.zeng.
 * @since 2018-03-28.
 */
public interface IRemindService extends IBaseService<RemindEntity> {


    /**
     * 保存或者修改提醒设置
     *
     * @param remind 提醒对象
     * @return
     */
    boolean saveOrUpdate(RemindVo remind);


    /**
     * 查询设置信息
     *
     * @param memberId
     * @param appId
     * @return
     */
    RemindVo info(String memberId, String appId);
}