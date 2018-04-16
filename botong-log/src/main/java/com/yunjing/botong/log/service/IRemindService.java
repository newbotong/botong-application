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
     * @param submitType
     * @return
     */
    RemindVo info(String memberId, String appId, int submitType);

    /**
     * 根据用户id和appId 设置任务id
     *
     * @param taskId
     * @param memberId
     * @param appId
     * @return
     */
    boolean updateByMemberIdAndAppId(String taskId, String memberId, String appId);
}
